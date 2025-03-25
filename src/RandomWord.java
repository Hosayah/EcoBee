import java.util.Random;
public class RandomWord {
    private static final Random random = new Random();
    public static String getRandomWord(int selected) {
        
        String[][] words = {
            {
                "reduce", "water", "tree", "pollution", "green", "clean", "air",
                "solar", "wind", "organic", "plastic", "energy", "waste", "nature", "eco", "wildlife", "forest",
                "garden", "battery", "renewable", "carbon", "rain", "river", "planet",
                "ocean", "climate", "habitat", "flora", "fauna", "soil", "fossil", "fuel", "biofuel",
                "wildlife", "shade", "reduce", "friendly", "drive", "walk", "waterway", "park", "plant",
                "bin", "landfill", "garbage", "dump", "oil", "pulp", "runoff", "toxic"
            },
            {
                "sustainability", "conservation", "agriculture", "emission", "footprint", "geothermal", "hydropower", "biomass",
                "combustible", "microplastics", "greenhouse", "permaculture", "afforestation",
                "deforestation", "aquaculture", "ecosystem", "permafrost", "overfishing", "sustainable",
                "biodegradable", "marine", "land", "alternative", "electronic", "hazardous", "methane", "humus", "aseptic", "contaminate", "ferrous",
                "incinerate", "corrosive", "postconsumer"
            },
            {
                "photovoltaic", "anthropogenic", "bioaccumulation", "bioremediation",
                "desertification", "geospatial", "infrastructure", "mitigate",
                "resilience", "adaptation", "urbanize", "management", "procure", "offset", "restoration",
                "transportation", "modeling", "biodiversity", "conservation", 
                "lithosphere", "stratosphere", "biochemistry", "geotropism", "electrify",
                "polyethylene", "microorganism", "petroleum", "thermoplastic", 
            }
        };
        return words[selected][random.nextInt(words[selected].length)];
    }
}
