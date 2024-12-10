### Building and running your application

#### Using Docker Compose

When you're ready, start your application by running:
`docker compose up --build`.

#### Using Command Line

To build the Docker image, run:
`docker build -t myapp .`.

Then, start your application with the following command:
`docker run -p 8080:8080 myapp`.

### Using Maven

You can also use Maven to package your application. Run the following command:
`mvn clean package`.
This will generate a JAR file in the `target` directory. You can then run your application with:
`java -jar target/<name-of-your-jar>.jar`.
Replace `<name-of-your-jar>` with `users-0.0.1-SNAPSHOT.jar`.

Your application will be available at http://localhost:8080.