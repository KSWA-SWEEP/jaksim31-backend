package com.sweep.jaksim31;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.sweep.jaksim31.util.ExecutionTimeTestExecutionListener;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, ExecutionTimeTestExecutionListener.class})
public class EmbeddedMongoDBTest {

    @Autowired
    MongoTemplate mongoTemplate;

    @BeforeAll
    public void init() {
        DBObject objectToSave = BasicDBObjectBuilder.start()
                .add("key", "geunho")
                .add("baseName", "희희")
                .get();

        // when
        mongoTemplate.save(objectToSave, "collection");
//        memberRepository.save(members);
    }

    @Nested
    @DisplayName("Embedded MongoDB 연동 테스트")
    class EmbeddedMongo {
        @Test
        @DisplayName("Embedded MongoDB 연동 테스트 1")
        void test() {
            // then
            assertThat(mongoTemplate.findAll(DBObject.class, "collection")).extracting("key")
                    .containsOnly("geunho");

            for (DBObject tmp : mongoTemplate.findAll(DBObject.class, "collection")) {
                System.out.println(tmp.toString());
            }
        }

        @Test
        @DisplayName("Embedded MongoDB 연동 테스트 2")
        void baseNmTest() {
            for (DBObject tmp : mongoTemplate.findAll(DBObject.class, "collection")) {
                System.out.println(tmp.toString());
            }

            Query query = Query.query(Criteria.where("baseName").is("희희"));
            assertThat(mongoTemplate.findOne(query, DBObject.class, "collection"))
                    .extracting("baseName")
                    .isEqualTo("희희");
        }
    }



}
