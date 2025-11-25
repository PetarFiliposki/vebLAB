package mk.ukim.finki.wp.lab.bootstrap;

import jakarta.annotation.PostConstruct;
import mk.ukim.finki.wp.lab.model.Chef;
import mk.ukim.finki.wp.lab.model.Dish;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataHolder {

    public static List<Chef> chefs = new ArrayList<>();
    public static List<Dish> dishes = new ArrayList<>();

    @PostConstruct
    public void init() {

        chefs.add(new Chef(101L, "Alice", "Waters", "Pioneer of California cuisine and farm-to-table philosophy"));
        chefs.add(new Chef(102L, "Thomas", "Keller", "Renowned American chef known for French Laundry"));
        chefs.add(new Chef(103L, "Ferran", "Adrià", "Innovative Spanish chef, molecular gastronomy expert"));
        chefs.add(new Chef(104L, "Clare", "Smyth", "Award-winning British chef with modern fine dining focus"));
        chefs.add(new Chef(105L, "Yotam", "Ottolenghi", "Master of Middle Eastern and Mediterranean flavors"));

        dishes.add(new Dish("D1", "Ratatouille", "French", 30));
        dishes.add(new Dish("D2", "Pad Thai", "Thai", 25));
        dishes.add(new Dish("D3", "Tacos al Pastor", "Mexican", 20));
        dishes.add(new Dish("D4", "Pierogi", "Polish", 35));
        dishes.add(new Dish("D5", "Baklava", "Turkish", 40));
    }
}
