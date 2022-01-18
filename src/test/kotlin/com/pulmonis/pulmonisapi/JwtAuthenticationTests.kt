//package com.pulmonis.pulmonisapi
//
//
//import com.pulmonis.pulmonisapi.controller.JwtAuthenticationController
//import com.pulmonis.pulmonisapi.enums.UserStatus
//import com.pulmonis.pulmonisapi.hibernate.entities.JwtBlacklist
//import com.pulmonis.pulmonisapi.hibernate.entities.User
//import com.pulmonis.pulmonisapi.hibernate.repository.JwtBlacklistRepository
//import com.pulmonis.pulmonisapi.hibernate.repository.UserRepository
//import com.pulmonis.pulmonisapi.security.JwtUserDetailsService
//import com.pulmonis.pulmonisapi.security.UserService
//import com.pulmonis.pulmonisapi.security.bCryptPasswordEncoder
//import com.pulmonis.pulmonisapi.util.JwtTokenUtil
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
//import org.springframework.security.core.userdetails.User as UserDetails
//
//
//@SpringBootTest
//@WithMockUser("test")
//@AutoConfigureMockMvc
//class JwtAuthenticationTests {
//
//    @Autowired
//    private lateinit var mockMvc: MockMvc
//
//    @Autowired
//    private val jwtAuthenticationController: JwtAuthenticationController? = null
//
//    @MockBean
//    var userRepository: UserRepository? = null
//
//    @MockBean
//    var jwtBlacklistRepository: JwtBlacklistRepository? = null
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
//        assertThat(userRepository).isNotNull
//        assertThat(userDetailsService).isNotNull
//        assertThat(userService).isNotNull
//    }
//
//    private var username = "test@ut.ee"
//    private var password = "testing123"
//    private var firstName = "first"
//    private var lastName = "last"
//    private var status = UserStatus.active
//
//    private val newUser: User = User().also {
//        it.id = 1
//        it.email = username
//        it.password = bCryptPasswordEncoder().encode(password)
//        it.firstName = firstName
//        it.lastName = lastName
//        it.isAdmin = false
//        it.status = status
//    }
//    private val userDetails = UserDetails(username, password, listOf())
//    private val token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0IiwiaXNBZG1pbiI6ZmFsc2UsImV4cCI6MTYwNDg1NTQ5OSwidXNlcklkIjoxLCJpYXQiOjE2MDQ4NTM2OTl9.JfvyqOr0fnOmSPc-U3iGfyb7tsJBza-f9Ouy9oR1yYC08gg8d9vJLW99BeqZmhpHoQ-Kwuu826YPi4eK5YsINg"
//
//
//    //Function needed to check if any users were added. Built-in function is not sufficient for a kotlin application.
//    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
//
//
//    @Test
//    @Throws(Exception::class)
//    fun shouldSignUp() {
//        val payload = """{
//            "username": "$username",
//            "password": "$password",
//            "firstName": "$firstName",
//            "lastName": "$lastName"
//        }"""
//
//
//        val newUser: User = User().also {
//            it.email = username
//            it.password = bCryptPasswordEncoder().encode(password)
//            it.firstName = firstName
//            it.lastName = lastName
//            it.isAdmin = false
//        }
//        `when`(userDetailsService!!.save(any(User::class.java))).thenReturn(newUser)
//
//        mockMvc.perform(MockMvcRequestBuilders
//            .post("/user/signup")
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
//    fun shouldNotSignUpUsernameEmpty() {
//
//        val payload = """{
//            "username": "",
//            "password": "$password",
//            "firstName": "$firstName",
//            "lastName": "$lastName"
//        }"""
//
//        mockMvc.perform(MockMvcRequestBuilders
//            .post("/user/signup")
//            .accept(MediaType.APPLICATION_JSON)
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(payload))
//
//            .andExpect(MockMvcResultMatchers.status().`is`(400))
//            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
//    }
//
//
//    @Test
//    @Throws(Exception::class)
//    fun shouldNotSignUpPasswordEmpty() {
//
//        val payload = """{
//            "username": "$username",
//            "password": "",
//            "firstName": "$firstName",
//            "lastName": "$lastName"
//        }"""
//
//        mockMvc.perform(MockMvcRequestBuilders
//            .post("/user/signup")
//            .accept(MediaType.APPLICATION_JSON)
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(payload))
//
//            .andExpect(MockMvcResultMatchers.status().`is`(400))
//            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
//    }
//
//
//    @Test
//    @Throws(Exception::class)
//    fun shouldLogIn() {
//        val payload = """{
//            "username": "$username",
//            "password": "$password"
//        }"""
//
//        `when`(userRepository?.findByEmail(username))
//            .thenReturn(newUser)
//
//        `when`(userDetailsService?.loadUserByUsername(username))
//            .thenReturn(userDetails)
//
//        `when`(jwtTokenUtil?.generateToken(userDetails, newUser))
//            .thenReturn(token)
//
//        mockMvc.perform(MockMvcRequestBuilders
//            .post("/user/login")
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
//    fun shouldNotLogInUsernameEmpty() {
//
//        val payload = """{
//            "username": "",
//            "password": "$password"
//        }"""
//
//        mockMvc.perform(MockMvcRequestBuilders
//            .post("/user/login")
//            .accept(MediaType.APPLICATION_JSON)
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(payload))
//
//            .andExpect(MockMvcResultMatchers.status().`is`(412))
//            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
//    }
//
//
//    @Test
//    @Throws(Exception::class)
//    fun shouldNotLogInPasswordEmpty() {
//
//        val payload = """{
//            "username": "$username",
//            "password": ""
//        }"""
//
//        mockMvc.perform(MockMvcRequestBuilders
//            .post("/user/login")
//            .accept(MediaType.APPLICATION_JSON)
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(payload))
//
//            .andExpect(MockMvcResultMatchers.status().`is`(412))
//            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun shouldNotLogInInvalidCredentials() {
//        val payload = """{
//            "username": "$username",
//            "password": "invalidpassword"
//        }"""
//
//        `when`(userRepository?.findByEmail(username))
//            .thenReturn(newUser)
//
//        `when`(userDetailsService?.loadUserByUsername(username))
//            .thenReturn(userDetails)
//
//        `when`(jwtTokenUtil?.generateToken(userDetails, newUser))
//            .thenReturn(token)
//
//        mockMvc.perform(MockMvcRequestBuilders
//            .post("/user/login")
//            .accept(MediaType.APPLICATION_JSON)
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(payload))
//
//            .andExpect(MockMvcResultMatchers.status().`is`(400))
//            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
//    }
//
//
//    @Test
//    @Throws(Exception::class)
//    fun shouldLogOut() {
//
//        `when`(jwtTokenUtil!!.formatToken("Bearer $token"))
//            .thenReturn(token)
//
//        `when`(jwtBlacklistRepository!!.save(any(JwtBlacklist::class.java)))
//            .thenReturn(null)
//
//        mockMvc.perform(MockMvcRequestBuilders
//            .delete("/user/logout")
//            .header("authorization", "Bearer $token")
//            .accept(MediaType.APPLICATION_JSON)
//            .contentType(MediaType.APPLICATION_JSON))
//
//            .andExpect(MockMvcResultMatchers.status().isOk)
//            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
//    }
//}
