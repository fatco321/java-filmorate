package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.JavaFilmorateApplication;
import ru.yandex.practicum.filmorate.service.RecommendationsService;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(
        classes = JavaFilmorateApplication.class
)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@TestPropertySource(
        locations =
                "classpath:application-integrationtest.properties"
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql({"/import_data.sql"})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecommendationsService recommendationsService;

    @Test
    void getRecommendationsWithPositiveMarkTest() throws Exception{
        String json = "[{\"id\":3,\"name\":\"Film_3\",\"description\":\"Description_3\"," +
                "\"releaseDate\":\"1970-10-23\",\"duration\":100,\"usersLike\":[],\"genres\":[]," +
                "\"mpa\":{\"id\":1,\"name\":\"G\"},\"rate\":0,\"directors\":[{\"id\":1,\"name\":\"Director_1\"}]," +
                "\"markAvg\":7.0}," +
                "{\"id\":4,\"name\":\"Film_4\",\"description\":\"Description_4\"," +
                "\"releaseDate\":\"1970-10-23\",\"duration\":100,\"usersLike\":[],\"genres\":[]," +
                "\"mpa\":{\"id\":4,\"name\":\"R\"},\"rate\":0,\"directors\":[{\"id\":2,\"name\":\"Director_2\"}]," +
                "\"markAvg\":7.0}]";
        this.mockMvc.perform(MockMvcRequestBuilders.get("/users/1/recommendations?mark=true"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().json(json, true))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

    }

}