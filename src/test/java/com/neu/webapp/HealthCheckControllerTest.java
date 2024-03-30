package com.neu.webapp;

import com.neu.webapp.rest.GlobalExceptionHandler;
import com.neu.webapp.rest.HealthCheckRestController;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class HealthCheckControllerTest {
    private MockMvc mockMvc;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query mockQuery;

    @InjectMocks
    private HealthCheckRestController healthCheckRestController;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(healthCheckRestController)
                                      .setControllerAdvice(new GlobalExceptionHandler())
                                      .build();
    }

    @Test
    public void whenDatabaseIsOkAndNoPayload_thenReturnStatusOk() throws Exception {
        doReturn(mockQuery).when(entityManager).createNativeQuery("SELECT 1");
        this.mockMvc.perform(get("/healthz").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                    .andExpect(header().string("Cache-Control", "no-cache, no-store, must-revalidate"));
    }

    @Test
    public void whenDatabaseIsOkAndUnexpectedPayload_thenReturnStatusBadRequest() throws Exception {
        doReturn(mockQuery).when(entityManager).createNativeQuery("SELECT 1");
        String payLoad = "{\"key\":\"Some payload\"}";
        this.mockMvc.perform(get("/healthz").contentType(MediaType.APPLICATION_JSON).content(payLoad))
                    .andExpect(status().isBadRequest());
    }

    @Test
    public void whenDatabaseIsOkAndQueryParameterPresent_thenReturnStatusBadRequest() throws Exception {
        doReturn(mockQuery).when(entityManager).createNativeQuery("SELECT 1");
        this.mockMvc.perform(get("/healthz?unexpectedParam=value").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
    }

    @Test
    public void whenDatabaseIsDown_thenReturnStatusServiceUnavailable() throws Exception {
        doThrow(new PersistenceException("Invalid Database Connection")).when(entityManager)
                                                                        .createNativeQuery("SELECT 1");

        this.mockMvc.perform(get("/healthz").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isServiceUnavailable());
    }

    @Test
    public void whenPostMethodNotAllowed_thenReturnMethodNotAllowedStatus() throws Exception {
        mockMvc.perform(post("/healthz").contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isMethodNotAllowed())
               .andExpect(header().string("Cache-Control", "no-cache, no-store, must-revalidate"));
    }

    @Test
    public void whenPutMethodNotAllowed_thenReturnMethodNotAllowedStatus() throws Exception {
        this.mockMvc.perform(put("/healthz").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isMethodNotAllowed())
                    .andExpect(header().string("Cache-Control", "no-cache, no-store, must-revalidate"));
    }

    @Test
    public void whenDeleteMethodNotAllowed_thenReturnMethodNotAllowedStatus() throws Exception {
        this.mockMvc.perform(delete("/healthz").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isMethodNotAllowed())
                    .andExpect(header().string("Cache-Control", "no-cache, no-store, must-revalidate"));
    }

    @Test
    public void whenPatchMethodNotAllowed_thenReturnMethodNotAllowedStatus() throws Exception {
        this.mockMvc.perform(patch("/healthz").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isMethodNotAllowed())
                    .andExpect(header().string("Cache-Control", "no-cache, no-store, must-revalidate"));
    }
}
