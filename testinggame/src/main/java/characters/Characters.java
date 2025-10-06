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
        
        // Helper methods for managing unique values
        public uniqueValue getUniqueValue(String name) {
            if (uniqueValues == null) return null;
            for (uniqueValue uv : uniqueValues) {
                if (uv.getName().equals(name)) {
                    return uv;
                }
            }
            return null;
        }
        
        public void setUniqueValue(String name, String value) {
            if (uniqueValues == null) {
                uniqueValues = new ArrayList<>();
            }
            
            uniqueValue existing = getUniqueValue(name);
            if (existing != null) {
                existing.setValue(value);
            } else {
                uniqueValues.add(new uniqueValue(name, value));
            }
        }
        
        public void addToUniqueValue(String name, float amount) {
            uniqueValue existing = getUniqueValue(name);
            if (existing != null) {
                try {
                    float currentValue = Float.parseFloat(existing.getValue());
                    existing.setValue(String.valueOf(currentValue + amount));
                } catch (NumberFormatException e) {
                    // If not a number, treat as 0
                    existing.setValue(String.valueOf(amount));
                }
            } else {
                setUniqueValue(name, String.valueOf(amount));
            }
        }
        
        public float getUniqueValueAsFloat(String name) {
            uniqueValue uv = getUniqueValue(name);
            if (uv != null) {
                try {
                    return Float.parseFloat(uv.getValue());
                } catch (NumberFormatException e) {
                    return 0f;
                }
            }
            return 0f;
        }

        @Override
        public String toString() {
            return "character{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", atk=" + atk +
                    ", matk=" + matk +
                    ", def=" + def +
                    ", res=" + res +
                    ", spd=" + spd +
                    ", AV=" + AV +
                    ", hp=" + hp +
                    ", mp=" + mp +
                    ", uniqueValues=" + uniqueValues +
                    '}';
        }
    }

    public class CharacterRegistry {
        private static final Map<String, Characters.character> registry = new HashMap<>();

        private static void register(Characters.character character) {
            registry.put(character.getName(), character);
        }

        public static Characters.character getByName(String name) {
            return registry.get(name);
        }

        public static Collection<Characters.character> getAll() {
            return registry.values();
        }

        public static void init() {
            // Create and register Hero with berserker talent using utility method
            register(characters.SpecialTalents.createBerserker(
                    1,
                    "Hero",
                    100,  // atk
                    30,  // matk
                    20,  // def
                    10,  // res
                    15,  // spd
                    300, // hp
                    200   // mp
            ));
            
            // Create and register Hero2 with mana shield talent using utility method
            Characters.character hero2 = characters.SpecialTalents.createMage(
                    4,
                    "Hero2",
                    100,  // atk
                    35,   // matk
                    18,   // def
                    12,   // res
                    12,   // spd
                    180, // hp
                    500   // mp
            );
            hero2.setUniqueValue("Regeneration","25");
            hero2.setUniqueValue("MpRegeneration","25");
            register(hero2);

            // Create and register Enemy
            register(new Characters.character(
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
            ));

            // Create and register Enemy2
            register(new Characters.character(
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
            ));
            
            // Create and register Hero3 (Flamita) with Burning Rage
            register(new Characters.character(
                    5,
                    "Flamita",
                    50,  // atk
                    35,   // matk
                    18,   // def
                    12,   // res
                    12,   // spd
                    500, // hp
                    0,   // mp
                    new ArrayList<uniqueValue>() {{
                        add(new uniqueValue("Burning rage", "0"));
                    }}
            ));
            
            // Create and register Hero4 (Pieberry)
            register(new Characters.character(
                    6,
                    "Pieberry",
                    100,  // atk
                    35,   // matk
                    18,   // def
                    12,   // res
                    10,   // spd
                    300, // hp
                    500,   // mp
                    new ArrayList<>(){{
                        add(new uniqueValue("MpRegeneration", "100"));
                    }}
            ));
        }
    }
}
