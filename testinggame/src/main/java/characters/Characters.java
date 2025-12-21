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
        String talentDiscription;
        ArrayList<uniqueValue> uniqueValues;

        // Original constructor (no talent description)
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
            this.talentDiscription = "";
        }

        // New constructor with talent description
        public character(int id, String name, float atk, float matk, float def, float res, float spd,
                         float hp, float mp, String talentDiscription, ArrayList<uniqueValue> uniqueValues) {
            this(id, name, atk, matk, def, res, spd, hp, mp, uniqueValues);
            this.talentDiscription = talentDiscription;
        }
        // In Characters.java
        public character(character other) {
            this.id = other.id;
            this.name = other.name;
            this.atk = other.atk;
            this.matk = other.matk;
            this.def = other.def;
            this.res = other.res;
            this.spd = other.spd;
            this.AV = other.AV;
            this.hp = other.hp;
            this.mp = other.mp;
            // Deep copy uniqueValues list
            if(other.uniqueValues!=null) {
                this.uniqueValues = new ArrayList<>();
                for (uniqueValue uv : other.uniqueValues) {
                    this.uniqueValues.add(new uniqueValue(uv.getName(), uv.getValue()));
                }
            }
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
        public void updateAV(){this.AV = 1000/spd;}

        public float getHp() { return hp; }
        public void setHp(float hp) { this.hp = hp; }

        public float getMp() { return mp; }
        public void setMp(float mp) { this.mp = mp; }

        public ArrayList<uniqueValue> getUniqueValues() { return uniqueValues; }
        public void setUniqueValues(ArrayList<uniqueValue> uniqueValues) { this.uniqueValues = uniqueValues; }

        public String getTalentDiscription() {
            return talentDiscription;
        }

        public void setTalentDiscription(String talentDiscription) {
            this.talentDiscription = talentDiscription;
        }

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
                    existing.setValue(String.valueOf(Math.max(0,currentValue + amount)));
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
//        public character copy(){
//            return new character(this.id,this.name,this.atk,this.matk,this.def,this.res,this.spd,this.getAV())
//        }
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
                    200,  // mp
                    "No...You see...I don't even have a name"    // talentDiscription template (fill later)
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
                    500,  // mp
                    "Have Regeneration 25(Heal 25 Hp at the start of the turn)\n" +
                    "Have MpRegeneration 25(Regen 25 Mp at the start of the turn)"    // talentDiscription template (fill later)
            );
            hero2.setUniqueValue("Regeneration","25");
            hero2.setUniqueValue("MpRegeneration","25");
            register(hero2);

            // Create and register Enemy
//            register(new Characters.character(
//                    2,
//                    "Enemy",
//                    50,  // atk
//                    30,  // matk
//                    20,  // def
//                    10,  // res
//                    15,  // spd
//                    2000, // hp
//                    50,  // mp
//                    new ArrayList<>()
//            ));
//
//            // Create and register Enemy2
//            register(new Characters.character(
//                    3,
//                    "Enemy2",
//                    50,  // atk
//                    30,  // matk
//                    20,  // def
//                    10,  // res
//                    15,  // spd
//                    2000, // hp
//                    50,  // mp
//                    new ArrayList<>()
//            ));
            
            // Create and register Hero3 (Flamita) with Burning Rage
            register(new Characters.character(
                    5,
                    "Flamita",
                    100,  // atk
                    35,   // matk
                    18,   // def
                    12,   // res
                    12,   // spd
                    500, // hp
                    0,   // mp
                    "Has Burning rage: Increase for each point of Hp reduce,reduce burning rage instead of Hp if Hp=1",  // talentDiscription template (fill later)
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
                    "Have MpRegeneration 100(Regen 100 Mp at the start of the turn)",  // talentDiscription template (fill later)
                    new ArrayList<>(){{
                        add(new uniqueValue("MpRegeneration", "100"));
                    }}
            ));

            // Create and register another fuking hero
            register(new Characters.character(
                    7,
                    "Ina",
                    100,  // atk
                    35,   // matk
                    18,   // def
                    12,   // res
                    10,   // spd
                    300, // hp
                    15,// mp
                    "Her Mp will always stay at 5 at the start of the battle",  // talentDiscription template (fill later)
                    new ArrayList<>()
            ));

            // Create and register another fuking hero
            register(new Characters.character(
                    8,
                    "Leuna",
                    100,  // atk
                    35,   // matk
                    18,   // def
                    12,   // res
                    10,   // spd
                    300, // hp
                    300,// mp
                    "Have MpRegeneration 100(Regen 100 Mp at the start of the turn)\n" +
                            "With her in party, for each 10 point of Mp consume by allies increase 1 point of PartyMp",  // talentDiscription template (fill later)
                    new ArrayList<uniqueValue>() {{
                        add(new uniqueValue("MpRegeneration", "100"));
                    }}
            ));

            register(new Characters.character(
                    9,
                    "Flatina",
                    100,  // atk
                    35,   // matk
                    18,   // def
                    12,   // res
                    10,   // spd
                    400, // hp
                    0,// mp
                    "Has Burning rage: Increase for each point Hp reduce,reduce burning rage instead of Hp if Hp=1",  // talentDiscription template (fill later)
                    new ArrayList<uniqueValue>() {{
                        add(new uniqueValue("Burning rage", "0"));
                    }}
            ));
            register(new Characters.character(
                    10,
                    "Chigon",
                    100,  // atk
                    35,   // matk
                    18,   // def
                    12,   // res
                    10,   // spd
                    400, // hp
                    0,// mp
                    "With her in party for each 10 point of damage by allies increase 1 point of PartyMp",  // talentDiscription template (fill later)
                    new ArrayList<uniqueValue>()
            ));

            register(new Characters.character(
                    11,
                    "Lucia",
                    200,  // atk
                    35,   // matk
                    18,   // def
                    12,   // res
                    10,   // spd
                    2000, // hp
                    500,// mp
                    "If you can read this line, then the dev are fuck",  // talentDiscription template (fill later)
                    new ArrayList<uniqueValue>() {{
                        add(new uniqueValue("Elysion Regeneration", "0"));
                    }}
            ));
            register(new Characters.character(
                    12,
                    "Litaru",
                    100,  // atk
                    35,   // matk
                    18,   // def
                    12,   // res
                    10,   // spd
                    300, // hp
                    300,// mp
                    "",  // talentDiscription template (fill later)
                    new ArrayList<uniqueValue>()
            ));
            register(new Characters.character(
                    14,
                    "Litaru ",
                    100,  // atk
                    35,   // matk
                    18,   // def
                    12,   // res
                    10,   // spd
                    500, // hp
                    300,// mp
                    "",  // talentDiscription template (fill later)
                    new ArrayList<uniqueValue>()
            ));
            register(new Characters.character(
                    13,
                    "Azar",
                    100,  // atk
                    35,   // matk
                    18,   // def
                    12,   // res
                    10,   // spd
                    450, // hp
                    200,// mp
                    "",  // talentDiscription template (fill later)
                    new ArrayList<uniqueValue>()
            ));
        }
    }
}
