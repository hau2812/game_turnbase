package characters;

import abilities.Ability;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface Characters {
    public class uniqueValue {
        String name;
        String value;

        public uniqueValue(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }

    public class character {
        int id;
        String name;
        float atk;
        float matk;
        float def;
        float res;
        float spd;
        float AV;
        float hp;
        float mp;
        ArrayList<uniqueValue> uniqueValues;

        public character(int id, String name, float atk, float matk, float def, float res, float spd, float hp, float mp, ArrayList<uniqueValue> uniqueValues) {
            this.id = id;
            this.name = name;
            this.atk = atk;
            this.matk = matk;
            this.def = def;
            this.res = res;
            this.spd = spd;
            this.AV = 1000 / spd;
            this.hp = hp;
            this.mp = mp;
            this.uniqueValues = uniqueValues;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public float getAtk() { return atk; }
        public void setAtk(float atk) { this.atk = atk; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public float getDef() { return def; }
        public void setDef(float def) { this.def = def; }

        public float getMatk() { return matk; }
        public void setMatk(float matk) { this.matk = matk; }

        public float getSpd() { return spd; }
        public void setSpd(float spd) { this.spd = spd; }

        public float getRes() { return res; }
        public void setRes(float res) { this.res = res; }

        public float getAV() { return AV; }
        public void setAV(float AV) { this.AV = AV; }

        public float getHp() { return hp; }
        public void setHp(float hp) { this.hp = hp; }

        public float getMp() { return mp; }
        public void setMp(float mp) { this.mp = mp; }

        public ArrayList<uniqueValue> getUniqueValues() { return uniqueValues; }
        public void setUniqueValues(ArrayList<uniqueValue> uniqueValues) { this.uniqueValues = uniqueValues; }
    }

    public class CharacterRegistry {
        private static final Map<String, Characters.character> registry = new HashMap<>();

        public static Characters.character getByName(String name) {
            return registry.get(name);
        }

        public static Collection<Characters.character> getAll() {
            return registry.values();
        }

        public static void init() {
            // Example: create a Hero character

            Characters.character hero = new Characters.character(
                    1,
                    "Hero",
                    500,  // atk
                    30,  // matk
                    20,  // def
                    10,  // res
                    15,  // spd
                    2000, // hp
                    500,  // mp
                    new ArrayList<>()
            );
            Characters.character hero2 = new Characters.character(
                    4,
                    "Hero2",
                    480,  // atk
                    35,   // matk
                    18,   // def
                    12,   // res
                    14,   // spd
                    1800, // hp
                    150,   // mp
                    new ArrayList<>()
            );
            Characters.character enemy = new Characters.character(
                    2,
                    "Enemy",
                    50,  // atk
                    30,  // matk
                    20,  // def
                    10,  // res
                    15,  // spd
                    2000, // hp
                    50,  // mp
                    new ArrayList<>()
            );

            Characters.character enemy2 = new Characters.character(
                    3,
                    "Enemy2",
                    50,  // atk
                    30,  // matk
                    20,  // def
                    10,  // res
                    15,  // spd
                    2000, // hp
                    50,  // mp
                    new ArrayList<>()
            );
            


            // Register the character
            registry.put(hero.getName(), hero);
            registry.put(hero2.getName(), hero2);
            registry.put(enemy.getName(), enemy);
            registry.put(enemy2.getName(), enemy2);
        }
    }
}
