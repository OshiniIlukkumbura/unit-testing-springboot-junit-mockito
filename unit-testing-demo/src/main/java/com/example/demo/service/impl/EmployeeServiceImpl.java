package com.example.demo.service.impl;

import com.example.demo.dao.entity.Employee;
import com.example.demo.dao.repository.EmployeeRepository;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.EmployeeService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        checkDuplicateEmail(employee.getEmail());
        return employeeRepository.save(employee);
    }

    @Override
    public List<Employee> getAllEmployees(){
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    @Transactional
    @Override
    public Employee updateEmployee(Long id, Employee employee) {
        Employee existing = getEmployeeById(id);
        if (!existing.getEmail().equals(employee.getEmail())) {
            checkDuplicateEmail(employee.getEmail());
        }
        existing.setFirstName(employee.getFirstName());
        existing.setLastName(employee.getLastName());
        existing.setEmail(employee.getEmail());
        return employeeRepository.save(existing);
    }

    @Override
    public void deleteEmployee(long id) {
        Employee existing = getEmployeeById(id); // throws ResourceNotFoundException if not found
        employeeRepository.delete(existing);
    }

    private void checkDuplicateEmail(String email) {
        employeeRepository.findByEmail(email).ifPresent(e -> {
            throw new DuplicateResourceException("Employee already exists with email: " + email);
        });
    }
}
