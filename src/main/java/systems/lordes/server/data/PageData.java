package systems.lordes.server.data;

import lombok.Data;
import systems.lordes.server.gen.api.Page;

@Data
abstract class PageData {
    private Page page;
}
