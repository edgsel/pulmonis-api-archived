//package com.pulmonis.pulmonisapi
//
//import com.pulmonis.pulmonisapi.controller.StatusController
//import org.assertj.core.api.Assertions.assertThat
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.http.MediaType
//import org.springframework.security.test.context.support.WithMockUser
//import org.springframework.test.web.servlet.MockMvc
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers
//
//
//@SpringBootTest
//@WithMockUser("test")
//@AutoConfigureMockMvc
//class PulmonisApiApplicationTests {
//
//    @Autowired
//    private lateinit var mockMvc: MockMvc
//
//    @Autowired
//    private val statusController: StatusController? = null
//
//
//    @Test
//    fun contextLoads() {
//        assertThat(statusController).isNotNull
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun shouldReturnStatus() {
//        mockMvc.perform(MockMvcRequestBuilders
//            .get("/status")
//            .accept(MediaType.APPLICATION_JSON)
//            .contentType(MediaType.APPLICATION_JSON))
//
//            .andExpect(MockMvcResultMatchers.status().`is`(200))
//            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(MockMvcResultMatchers.content().string("{\"data\":{\"ok\":true},\"errors\":null,\"status\":200}"))
//    }
//
////    @Test
////    fun checkConnection() {
////        val DB_HOST = System.getenv("ENV_DATABASE_HOST")
////        val DB_PORT = System.getenv("ENV_DATABASE_PORT")
////        val DB_USERNAME = System.getenv("ENV_DATABASE_USERNAME")
////        val DB_NAME = System.getenv("ENV_DATABASE_NAME")
////        val DB_PASSWORD = System.getenv("ENV_DATABASE_PASSWORD")
////        val DB_CONNECTION_STRING = "jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false"
////
////        try {
////            println("Connecting to db: $DB_CONNECTION_STRING")
////            DriverManager.getConnection(DB_CONNECTION_STRING, DB_USERNAME, DB_PASSWORD)
////            println("Success")
////        } catch (e: Exception) {
////            println(e.printStackTrace())
////        }
////    }
//
//}
