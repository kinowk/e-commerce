plugins {
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // jpa
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    // querydsl
    api("com.querydsl:querydsl-jpa::jakarta")
    annotationProcessor("com.querydsl:querydsl-apt::jakarta")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    // jdbc-mysql
    runtimeOnly("com.mysql:mysql-connector-j")

    testImplementation("org.testcontainers:mysql")

    testFixturesImplementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testFixturesImplementation("org.testcontainers:mysql")

    // JSON type Column
    api("io.hypersistence:hypersistence-utils-hibernate-63")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-jakarta-xmlbind-annotations")
}
