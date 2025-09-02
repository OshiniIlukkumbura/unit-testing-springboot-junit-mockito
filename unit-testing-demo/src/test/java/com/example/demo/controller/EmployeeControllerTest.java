package com.example.demo.controller;

import com.example.demo.dao.entity.Employee;
import com.example.demo.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)     // Enables Mockito in JUnit 5.
class EmployeeControllerTest {

    // Mocks the service layer so the controller is tested in isolation (no database calls).
    @Mock
    private EmployeeService employeeService;

    // Injects the mocked EmployeeService into your controller.
    @InjectMocks
    private EmployeeController employeeController;

    // simulates HTTP requests to your controller, like a fake web client.
    private MockMvc mockMvc;

    // Initializes mockMvc with a standalone setup of your controller.
    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
    }

    @Test
    void createEmployee_ShouldReturnSavedEmployee() throws Exception {
        Employee employee = new Employee(1L, "Amal", "Perera", "amal.perera@gmail.com");

        when(employeeService.saveEmployee(any(Employee.class))).thenReturn(employee);

        // Status 201 Created.
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Amal\",\"lastName\":\"Perera\",\"email\":\"amal.perera@gmail.com\"}"))
                .andExpect(status().isCreated()) // ✅ expect 201
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Amal"))
                .andExpect(jsonPath("$.lastName").value("Perera"))
                .andExpect(jsonPath("$.email").value("amal.perera@gmail.com"));
    }

    /**
     * Expects:
     *             Status 200 OK.
     *             JSON array has size 2.
     *             First employee’s firstName is "Amal".
     *             Second employee’s email is "kamal@gmail.com".
     * **/
    @Test
    void getAllEmployees_ShouldReturnList() throws Exception {
        List<Employee> employees = List.of(
                new Employee(1L, "Amal", "Perera", "amal@gmail.com"),
                new Employee(2L, "Kamal", "Fernando", "kamal@gmail.com")
        );

        when(employeeService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Amal"))
                .andExpect(jsonPath("$[1].email").value("kamal@gmail.com"));
    }

    /**
     * Expects:
     * Status 200 OK.
     * JSON has id=1, firstName="Amal", etc.
     * */
    @Test
    void getEmployee_ShouldReturnEmployee() throws Exception {
        Employee employee = new Employee(1L, "Amal", "Perera", "amal@gmail.com");

        when(employeeService.getEmployeeById(1L)).thenReturn(employee);

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Amal"))
                .andExpect(jsonPath("$.lastName").value("Perera"))
                .andExpect(jsonPath("$.email").value("amal@gmail.com"));
    }

    /**
     * Expects:
     * Status 200 OK.
     * JSON has updated email.
     * **/
    @Test
    void updateEmployee_ShouldReturnUpdatedEmployee() throws Exception {
        Employee updated = new Employee(1L, "Amal", "Perera", "amal.new@gmail.com");

        when(employeeService.updateEmployee(eq(1L), any(Employee.class))).thenReturn(updated);

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Amal\",\"lastName\":\"Perera\",\"email\":\"amal.new@gmail.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("amal.new@gmail.com"));
    }

    @Test
    void deleteEmployee_ShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isNoContent()); // Expects status 204 No Content.
    }

}
