import java.util.Random;
public class RandomWord {
    private static final Random random = new Random();
    public static String getRandomWord(int selected) {
        
        String[][] words = {
            {
                "reduce", "water", "tree", "pollution", "green", "clean", "air",
                "solar", "wind", "plastic", "energy", "waste", "nature", "wildlife", "forest",
                "garden", "battery", "renewable", "carbon", "rain", "river", "planet",
                "ocean", "climate", "habitat", "flora", "fauna", "soil", "fossil", "fuel",
                "wildlife", "shade", "reduce", "friendly", "drive", "walk", "waterway", "park", "plant",
                "landfill", "garbage", "dump", "oil", "pulp", "runoff", "fertile", "land", "crop"
            },
            {
                "sustainability", "conservation", "agriculture", "emission", "footprint", "geothermal", "hydropower", "biomass",
                "combustible", "microplastics", "greenhouse", "afforestation",
                "deforestation", "aquaculture", "ecosystem", "permafrost", "overfishing", "sustainable",
                "biodegradable", "marine",  "alternative", "electronic", "hazardous", "methane", "humus", "aseptic", "contaminate", "ferrous",
                "incinerate", "corrosive", "postconsumer", "hydroponics", "forestry", "rainwater", "acclimate"
            },
            {
                "anthropogenic", "bioaccumulation", "infrastructure", "mitigate", "aquaculture",
                "resilience", "adaptation", "urbanize", "management", "procure", "offset", "restoration",
                "transportation", "modeling", "biodiversity", "conservation", 
                "lithosphere", "stratosphere", "biochemistry", "geotropism", "electrify",
                "polyethylene", "microorganism", "petroleum", "thermoplastic", "hydroelectric"
            }
        };
        return words[selected][random.nextInt(words[selected].length)];
    }
}
