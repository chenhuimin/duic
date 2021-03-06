package io.zhudy.duic.repository.impl

import io.zhudy.duic.repository.ServerRepository
import io.zhudy.duic.repository.config.OracleConfiguration
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.ContextHierarchy
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests
import org.springframework.transaction.support.TransactionTemplate

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@ContextHierarchy(*[
(ContextConfiguration(locations = ["classpath:oracle-spring.xml"])),
(ContextConfiguration(classes = [OracleConfiguration::class]))
])
@TestPropertySource(properties = ["duic.dbms=Oracle"])
class OracleServerRepositoryTests : AbstractJUnit4SpringContextTests() {

    @Autowired
    lateinit var transactionTemplate: TransactionTemplate
    @Autowired
    lateinit var jdbcTemplate: NamedParameterJdbcTemplate
    @Autowired
    lateinit var serverRepository: ServerRepository

    @After
    fun clean() {
        transactionTemplate.execute {
            jdbcTemplate.update("DELETE FROM DUIC_SERVER", EmptySqlParameterSource.INSTANCE)
        }
    }

    @Test
    fun register() {
        val n = serverRepository.register("localhost", 1234).block()
        Assert.assertEquals(1, n)
    }

    @Test
    fun registerOnExists() {
        serverRepository.register("localhost", 1234).block()
        serverRepository.register("localhost", 1234).block()
    }

    @Test
    fun unregister() {
        serverRepository.register("localhost", 1234).block()
        val n = serverRepository.unregister("localhost", 1234).block()
        Assert.assertEquals(1, n)
    }

    @Test
    fun ping() {
        serverRepository.register("localhost", 1234).block()
        val n = serverRepository.ping("localhost", 1234).block()
        Assert.assertEquals(1, n)
    }

    @Test
    fun findServers() {
        serverRepository.register("localhost", 1234).block()
        val servers = serverRepository.findServers().collectList().block()
        Assert.assertTrue(servers.isNotEmpty())
    }
}