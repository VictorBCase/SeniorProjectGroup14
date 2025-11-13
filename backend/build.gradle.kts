plugins {
    id("java-library")
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation("com.google.zxing:core:3.5.2")
    implementation("com.google.zxing:javase:3.5.2")
    implementation("org.postgresql:postgresql:42.7.1")
    testImplementation(libs.junit.jupiter)

}


tasks.test {
    useJUnitPlatform()
}