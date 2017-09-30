package oliphant.com.library.kafkarestservice.commands;

public class AddBookCommand implements Command {

    private String correlationId;
    private String authorFirstName;
    private String authorLastName;

    public AddBookCommand(String authorFirstName, String authorLastName, String correlationId) {
        this.correlationId = correlationId;
        this.authorFirstName = authorFirstName;
        this.authorLastName = authorLastName;
    }
}
