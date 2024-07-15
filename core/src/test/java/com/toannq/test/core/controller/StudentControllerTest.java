package com.toannq.test.core.controller;

import com.toannq.test.commons.util.Constant;
import com.toannq.test.commons.util.ErrorCode;
import com.toannq.test.core.ApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StudentControllerTest extends ApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql(statements = {"truncate table student restart identity"})
    void createStudent_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/students")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Lê Tuấn Khanh",
                                  "age": 20,
                                  "major_id": 1,
                                  "mentor_id": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Lê Tuấn Khanh"))
                .andExpect(jsonPath("$.data.age").value(20));

        mockMvc.perform(MockMvcRequestBuilders.post("/students")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Nguyễn Xuân Thụy",
                                  "age": 21,
                                  "major_id": 1,
                                  "mentor_id": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.name").value("Nguyễn Xuân Thụy"))
                .andExpect(jsonPath("$.data.age").value(21));
    }

    @Test
    @Sql(statements = {"truncate table student restart identity",
            "insert into student(id, name, age, major_id, mentor_id) values(123, 'Lê Tuấn Khanh', 20, 1, 1)"})
    void updateStudent_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/students/123")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Nguyễn Xuân Thụy",
                                  "age": 22
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(123))
                .andExpect(jsonPath("$.data.name").value("Nguyễn Xuân Thụy"))
                .andExpect(jsonPath("$.data.age").value(22));
    }

    @Test
    @Sql(statements = {"truncate table student restart identity",
            "insert into student(id, name, age, major_id, mentor_id) values(123, 'Lê Tuấn Khanh', 20, 1, 1)"})
    void updateStudent_NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/students/124")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Nguyễn Xuân Thụy",
                                  "age": 22
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.meta.code").value(Constant.PREFIX_RESPONSE_CODE + ErrorCode.DATA_NOT_FOUND.code()));
    }

    @Test
    @Sql(statements = {"truncate table student restart identity",
            "insert into student(id, name, age, major_id, mentor_id) values(123, 'Lê Tuấn Khanh', 20, 1, 1)"})
    void deleteStudent_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/students/123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
   }

    @Test
    @Sql(statements = {"truncate table student restart identity",
            "insert into student(id, name, age, major_id, mentor_id) values(123, 'Lê Tuấn Khanh', 20, 1, 1)"})
    void deleteStudent_NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/students/124")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.meta.code").value(Constant.PREFIX_RESPONSE_CODE + ErrorCode.DATA_NOT_FOUND.code()));
    }

    @Test
    @Sql(statements = {"truncate table student restart identity",
            "insert into student(id, name, age, major_id, mentor_id) values(123, 'Lê Tuấn Khanh', 20, 1, 1)"})
    void getStudent_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/students/123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(123))
                .andExpect(jsonPath("$.data.name").value("Lê Tuấn Khanh"))
                .andExpect(jsonPath("$.data.age").value(20));

        mockMvc.perform(MockMvcRequestBuilders.get("/students/124")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.meta.code").value(Constant.PREFIX_RESPONSE_CODE + ErrorCode.DATA_NOT_FOUND.code()));
    }

    @Test
    @Sql(statements = {"truncate table student restart identity",
            "insert into student(id, name, age, major_id, mentor_id) values(123, 'Lê Tuấn Khanh', 20, 1, 1)"})
    void getDetailStudent_Success() throws Exception {
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/students/123/detail")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();
        mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(123))
                .andExpect(jsonPath("$.data.name").value("Lê Tuấn Khanh"))
                .andExpect(jsonPath("$.data.age").value(20))
                .andExpect(jsonPath("$.data.major_code").value("IT-1"))
                .andExpect(jsonPath("$.data.major_name").value("Information Technology 1"))
                .andExpect(jsonPath("$.data.mentor_name").value("Nguyễn Mạnh Hùng"));
    }

    @Test
    @Sql(statements = {"truncate table student restart identity",
            "insert into student(id, name, age, major_id, mentor_id) values(123, 'Lê Tuấn Khanh', 20, 2, 1)"})
    void getDetailStudent_MajorNotFound() throws Exception {
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/students/123/detail")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();
        mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.meta.code").value(Constant.PREFIX_RESPONSE_CODE + ErrorCode.DATA_NOT_FOUND.code()));
    }

    @Test
    @Sql(statements = {"truncate table student restart identity",
            "insert into student(id, name, age, major_id, mentor_id) values(123, 'Lê Tuấn Khanh', 20, 99, 1)"})
    void getDetailStudent_MajorTimeOut() throws Exception {
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/students/123/detail")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();
        mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.meta.code").value(Constant.PREFIX_RESPONSE_CODE + ErrorCode.MAJOR_SERVER_ERROR.code()));
    }

    @Test
    @Sql(statements = {"truncate table student restart identity",
            "insert into student(id, name, age, major_id, mentor_id) values(123, 'Lê Tuấn Khanh', 20, 1, 2)"})
    void getDetailStudent_MentorNotFound() throws Exception {
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/students/123/detail")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();
        mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.meta.code").value(Constant.PREFIX_RESPONSE_CODE + ErrorCode.DATA_NOT_FOUND.code()));
    }

    @Test
    @Sql(statements = {"truncate table student restart identity",
            "insert into student(id, name, age, major_id, mentor_id) values(123, 'Lê Tuấn Khanh', 20, 1, 99)"})
    void getDetailStudent_MentorTimeOut() throws Exception {
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/students/123/detail")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();
        mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.meta.code").value(Constant.PREFIX_RESPONSE_CODE + ErrorCode.MENTOR_SERVER_ERROR.code()));
    }
}
