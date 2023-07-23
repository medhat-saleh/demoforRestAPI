package com.example.demo.Controller;

import com.example.demo.Entity.Employee;
import com.example.demo.Repository.EmployeeRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class EmployeeController {
    private final EmployeeRepository repository;

    private final EmployeeModelAssembler assembler;

    EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {

        this.repository = repository;
        this.assembler = assembler;
    }
        @GetMapping("/employees")
    CollectionModel<EntityModel<Employee>> all() {

            List<EntityModel<Employee>> employees = repository.findAll().stream() //
                    .map(assembler::toModel) //
                    .collect(Collectors.toList());

            return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());   }
    @PostMapping("/employees")
    Employee newEmployee(@RequestBody Employee newemployee){
        return repository.save(newemployee);
    }
    @GetMapping("/employees/{id}")
    EntityModel<Employee> one(@PathVariable Long id) {

        Employee employee = repository.findById(id) //
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        return assembler.toModel(employee);
    }
    @PutMapping("/employees/{id}")
    Employee ReplaceEmployee(@RequestBody Employee newemployee ,@PathVariable Long id){
        return  repository.findById(id)
                .map(employee -> {employee.setName(newemployee.getName());
                    employee.setRole(newemployee.getRole());
                    return repository.save(employee);
                })
                .orElseGet(()->{newemployee.setId(id);
                    return repository.save(newemployee);
                });
    }
    @DeleteMapping("/employees/{id}")
    void deleteEmployee(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
