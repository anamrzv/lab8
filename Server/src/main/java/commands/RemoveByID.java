package commands;

import database.DataBaseManager;
import other.CollectionsKeeper;
import other.Person;
import other.ServerResponse;

import java.util.LinkedList;
import java.util.List;

public class RemoveByID extends Command {

    public RemoveByID(CollectionsKeeper dc) {
        super(dc);
    }

    @Override
    public ServerResponse execute(DataBaseManager manager, List<String> args) {
        if (args.size() != 2) {
            if (args.size() != 3) {
                return ServerResponse.builder().error("id error arguments").command("remove_by_id").build();
            }
            long id;
            try {
                id = Long.parseLong(args.get(2));
                if (id < 0)
                    return ServerResponse.builder().error("id error minus").command("remove_by_id").build();
            } catch (Exception e) {
                return ServerResponse.builder().error("id error validate").command("remove_by_id").build();
            }
            LinkedList<Person> people = collectionsKeeper.getPeople();
            Person person = people.stream()
                    .filter(x -> x.getId().equals(id))
                    .findFirst()
                    .orElse(null);
            int result;
            if (person == null)
                return ServerResponse.builder().message("id no id").command("remove_by_id").build();
            else {
                result = manager.deleteByIdFromBD(person, args);
                if (result == 1) {
                    people.remove(person);
                    return ServerResponse.builder().message("id remove success").command("remove_by_id").build();
                } else if (result == 0) {
                    return ServerResponse.builder().error("id creator error").command("remove_by_id").build();
                } else
                    return ServerResponse.builder().error("id db error").command("remove_by_id").build();
            }
        } else {
            return ServerResponse.builder().error("id error arguments.").command("remove_by_id").build();
        }
    }

    @Override
    public String getName() {
        return "remove_by_id";
    }

    @Override
    public String getDescription() {
        return "remove_by_id id : удалить элемент из коллекции по его id";
    }
}
