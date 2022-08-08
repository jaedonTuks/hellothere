package hellothere.config

import org.ff4j.FF4j
import org.ff4j.cache.FF4JCacheManager
import org.ff4j.cache.InMemoryCacheManager
import org.ff4j.springjdbc.store.EventRepositorySpringJdbc
import org.ff4j.springjdbc.store.FeatureStoreSpringJdbc
import org.ff4j.springjdbc.store.PropertyStoreSpringJdbc
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class FF4jConfig {

    // Config info can be found under at https://ff4j.org/#10min

    @Bean
    fun getFF4j(dataSource: DataSource): FF4j {
        val ff4j = FF4j()
        ff4j.featureStore = FeatureStoreSpringJdbc(dataSource)
        ff4j.propertiesStore = PropertyStoreSpringJdbc(dataSource)
        ff4j.eventRepository = EventRepositorySpringJdbc(dataSource)
        ff4j.audit(true)
        ff4j.autoCreate(false)

        ff4j.cache(InMemoryCacheManager())

        return ff4j
    }
}
