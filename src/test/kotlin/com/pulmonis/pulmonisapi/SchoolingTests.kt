//package com.pulmonis.pulmonisapi
//
//
//import com.pulmonis.pulmonisapi.controller.JwtAuthenticationController
//import com.pulmonis.pulmonisapi.controller.SchoolingController
//import com.pulmonis.pulmonisapi.hibernate.entities.Schooling
//import com.pulmonis.pulmonisapi.hibernate.entities.User
//import com.pulmonis.pulmonisapi.hibernate.repository.SchoolingRepository
//import com.pulmonis.pulmonisapi.hibernate.repository.UserRepository
//import com.pulmonis.pulmonisapi.security.JwtUserDetailsService
//import com.pulmonis.pulmonisapi.security.UserService
//import com.pulmonis.pulmonisapi.security.bCryptPasswordEncoder
//import com.pulmonis.pulmonisapi.util.JwtTokenUtil
//import java.math.BigDecimal
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter
//import org.assertj.core.api.Assertions.assertThat
//import org.junit.jupiter.api.Test
//import org.mockito.Mockito
//import org.mockito.Mockito.`when`
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.test.mock.mockito.MockBean
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
//class SchoolingTests {
//
//    @Autowired
//    private lateinit var mockMvc: MockMvc
//
//    @Autowired
//    private val schoolingController: SchoolingController? = null
//
//    @Autowired
//    private val jwtAuthenticationController: JwtAuthenticationController? = null
//
//    @MockBean
//    var userRepository: UserRepository? = null
//
//    @MockBean
//    var schoolingRepository: SchoolingRepository? = null
//
//    @MockBean
//    var userDetailsService: JwtUserDetailsService? = null
//
//    @MockBean
//    var userService: UserService? = null
//
//    @MockBean
//    val jwtTokenUtil: JwtTokenUtil? = null
//
//    @Test
//    fun contextLoads() {
//        assertThat(jwtAuthenticationController).isNotNull
//        assertThat(schoolingController).isNotNull
//        assertThat(userRepository).isNotNull
//        assertThat(userDetailsService).isNotNull
//        assertThat(userService).isNotNull
//    }
//
//    private var username = "test@ut.ee"
//    private var password = "testing123"
//    private var firstName = "first"
//    private var lastName = "last"
//
//    private val newUser: User = User().also {
//        it.id = 0
//        it.email = username
//        it.password = bCryptPasswordEncoder().encode(password)
//        it.firstName = firstName
//        it.lastName = lastName
//        it.isAdmin = false
//    }
//    private val token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0IiwiaXNBZG1pbiI6ZmFsc2UsImV4cCI6MTYwNDg1NTQ5OSwidXNlcklkIjoxLCJpYXQiOjE2MDQ4NTM2OTl9.JfvyqOr0fnOmSPc-U3iGfyb7tsJBza-f9Ouy9oR1yYC08gg8d9vJLW99BeqZmhpHoQ-Kwuu826YPi4eK5YsINg"
//
//
//    //Function needed to check if any users were added. Built-in function is not sufficient for a kotlin application.
//    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
//
//    private var title = "event1"
//    private var description = "Description"
//    private var address = "Address"
//    private var city = "City"
//    private var category = "category"
//    private var eventDateTime = "2007-12-03T10:15:30"
//    private var registrationDeadline = "2007-12-03T10:15:30"
//    private var userId = newUser.id
//    private var free = false
//    private var price = 20.0
//
//    private val newSchooling: Schooling = Schooling().also {
//        it.id = 1
//        it.title = title
//        it.description = description
//        it.address = address
//        it.city = city
//        it.category = category
//        it.eventDateTime = LocalDateTime.parse(eventDateTime.replace("T", " "), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
//        it.registrationDeadline = LocalDateTime.parse(registrationDeadline.replace("T", " "), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
//        it.user = newUser
//        it.free = free
//        it.price = BigDecimal(price)
//    }
//
//
//    @Test
//    @Throws(Exception::class)
//    fun shouldCreateSchooling() {
//        val payload = """{
//            "title": "$title",
//            "description": "$description",
//            "address": "$address",
//            "city": "$city",
//            "category": "$category",
//            "eventDateTime": "2007-12-03T10:15:30",
//            "registrationDeadline": "2007-12-03T10:15:30",
//            "userId": $userId,
//            "free": $free,
//            "price": $price
//        }"""
//
//        `when`(userRepository!!.findFirstById(userId))
//            .thenReturn(newUser)
//
//        `when`(schoolingRepository!!.save(any(Schooling::class.java)))
//            .thenReturn(newSchooling)
//
//        mockMvc.perform(MockMvcRequestBuilders
//            .post("/schooling/create")
//            .header("authorization", "Bearer $token")
//            .accept(MediaType.APPLICATION_JSON)
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(payload))
//
//            .andExpect(MockMvcResultMatchers.status().isOk)
//            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
//    }
//
//
//    @Test
//    @Throws(Exception::class)
//    fun shouldNotCreateSchoolingTitleEmpty() {
//        val payload = """{
//            "title": "",
//            "description": "$description",
//            "address": "$address",
//            "city": "$city",
//            "category": "$category",
//            "eventDateTime": "2007-12-03T10:15:30",
//            "registrationDeadline": "2007-12-03T10:15:30",
//            "userId": $userId,
//            "free": $free,
//            "price": $price
//        }"""
//
//        mockMvc.perform(MockMvcRequestBuilders
//            .post("/schooling/create")
//            .header("authorization", "Bearer $token")
//            .accept(MediaType.APPLICATION_JSON)
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(payload))
//
//            .andExpect(MockMvcResultMatchers.status().`is`(412))
//            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
//    }
//}
