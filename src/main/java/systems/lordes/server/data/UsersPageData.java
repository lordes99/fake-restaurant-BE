package systems.lordes.server.data;

import lombok.Data;
import systems.lordes.server.gen.api.Page;

import java.util.List;

@Data
public class UsersPageData {
    private List<UserData> users;
    private Page page;
}
