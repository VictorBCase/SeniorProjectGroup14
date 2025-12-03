plugins {
    id("java")
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("com.google.zxing:core:3.5.2")
    implementation("com.google.zxing:javase:3.5.2")
    testImplementation(libs.junit.jupiter)
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("org.json:json:20141113")

    implementation("org.java-websocket:Java-WebSocket:1.5.6")  //WebSocket library
    implementation ("com.google.code.gson:gson:2.11.0")// Gson for JSON 

}


tasks.test {
    useJUnitPlatform()
}
