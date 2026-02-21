package com.pumapunku.pet.api.controller;

import com.pumapunku.pet.application.CreatePetInteractor;
import com.pumapunku.pet.application.DeletePetInteractor;
import com.pumapunku.pet.application.UpdatePetInteractor;
import com.pumapunku.pet.domain.Pet;
import com.pumapunku.pet.domain.exception.AlreadyExistsException;
import com.pumapunku.pet.domain.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(PetController.class)
class PetControllerTest
{
    @MockBean
    private CreatePetInteractor createPetInteractor;

    @MockBean
    private UpdatePetInteractor updatePetInteractor;

    @MockBean
    private DeletePetInteractor deletePetInteractor;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createPet() throws Exception
    {
        Pet pet = new Pet("1", "Tammy");
        when(createPetInteractor.execute(pet)).thenReturn(pet);

        mockMvc.perform(post("/pet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"1\", \"name\":\"Tammy\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"id\":\"1\", \"name\":\"Tammy\"}"));
    }

    @Test
    void createPetWithException() throws Exception
    {
        Pet pet = new Pet("1", "Tammy");
        when(createPetInteractor.execute(pet)).thenThrow(new AlreadyExistsException("pet", "123"));

        mockMvc.perform(post("/pet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"1\", \"name\":\"Tammy\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().json("{\"message\":\"123 id already exists in the table\"}"));
    }

    @Test
    void updatePet() throws Exception
    {
        mockMvc.perform(put("/pet/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Tammy\"}"))
                .andExpect(status().isNoContent());

        verify(updatePetInteractor, times(1)).execute(new Pet("1", "Tammy"));
    }

    @Test
    void updatePetWithException() throws Exception
    {
        Pet pet = new Pet("12", "Tammy");
        doThrow(new NotFoundException("pet", "12")).when(updatePetInteractor).execute(pet);

        mockMvc.perform(put("/pet/12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Tammy\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\":\"12 not found!\"}"));
    }

    @Test
    void deletePet() throws Exception
    {
        mockMvc.perform(delete("/pet/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(deletePetInteractor, times(1)).execute("1");
    }
}
