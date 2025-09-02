package com.example.demo.service.impl;

import com.example.demo.dao.entity.Employee;
import com.example.demo.dao.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceImplTest {

    // creates a fake/mock version of EmployeeRepository.
    @Mock
    private EmployeeRepository employeeRepository;

    // creates an instance of EmployeeServiceImpl and injects the mocked EmployeeRepository into it.
    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @BeforeEach     // runs before every test method.
    void setUp() {
        MockitoAnnotations.openMocks(this);     // initializes the mock objects.
    }

    @Test
    void saveEmployee_ShouldReturnSavedEmployee() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("Amal");
        employee.setLastName("Perera");
        employee.setEmail("amal.perera@gmail.com");

        when(employeeRepository.save(employee)).thenReturn(employee);       // if save is called, return the same employee.

        Employee saved = employeeService.saveEmployee(employee);

        assertNotNull(saved);
        assertEquals("Amal", saved.getFirstName());
        verify(employeeRepository, times(1)).save(employee);    // Verifies that repository.save() was called once.
    }


    @Test
    void getEmployeeById_ShouldReturnEmployee() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("Amal");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployeeById(1L);

        assertNotNull(result);
        assertEquals("Amal", result.getFirstName());
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void getEmployeeById_ShouldThrowException_WhenNotFound() {
        // Configures employeeRepository.findById(2L) to return Optional.empty() (employee doesn’t exist).
        when(employeeRepository.findById(2L)).thenReturn(Optional.empty());

        // Expects a RuntimeException to be thrown.
        Exception exception = assertThrows(RuntimeException.class, () -> {
            employeeService.getEmployeeById(2L);
        });

        // Checks the exception message contains "not found".
        assertTrue(exception.getMessage().contains("not found"));
        verify(employeeRepository, times(1)).findById(2L);
    }

    @Test
    void deleteEmployee_ShouldCallRepositoryDelete() {
        Employee employee = new Employee();
        employee.setId(1L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(1L);

        // Verifies that repository.delete(employee) was called exactly once.
        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    void updateEmployee_ShouldReturnUpdatedEmployee() {
        Employee existing = new Employee();
        existing.setId(1L);
        existing.setFirstName("Amal");
        existing.setLastName("Perera");
        existing.setEmail("amal.perera@gmail.com");

        Employee updated = new Employee();
        updated.setFirstName("AmalUpdated");
        updated.setLastName("PereraUpdated");
        updated.setEmail("amal.updated@gmail.com");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(employeeRepository.save(any(Employee.class))).thenReturn(existing);

        Employee result = employeeService.updateEmployee(1L, updated);

        assertNotNull(result);
        assertEquals("AmalUpdated", result.getFirstName());
        assertEquals("PereraUpdated", result.getLastName());
        assertEquals("amal.updated@gmail.com", result.getEmail());

        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).save(existing);
    }


}