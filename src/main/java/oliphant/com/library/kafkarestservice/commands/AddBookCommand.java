package oliphant.com.library.kafkarestservice.commands;

public class AddBookCommand implements Command {

    //TODO: add correlationId

    private String authorFirstName;
    private String authorLastName;

    public AddBookCommand(String authorFirstName, String authorLastName) {
        this.authorFirstName = authorFirstName;
        this.authorLastName = authorLastName;
    }
}
