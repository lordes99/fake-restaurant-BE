package systems.lordes.server.data;

import lombok.Data;
import systems.lordes.server.gen.api.DayType;

import java.time.Instant;
import java.util.List;

@Data
public class WorkingDayData {
    private DayType day;
    private List<WorkingHourData> hours;

    @Data
    public static class WorkingHourData {
        private Instant open;
        private Instant close;
    }
}
