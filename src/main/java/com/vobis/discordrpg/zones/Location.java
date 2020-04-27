package com.vobis.discordrpg.zones;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private int x, y;

    public Location add(int x, int y) {
        return new Location(this.x + x, this.y + y);
    }
}
