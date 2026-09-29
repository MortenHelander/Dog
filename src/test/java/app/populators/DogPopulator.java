package app.populators;

import app.daos.DogDAO;
import app.entities.Dog;
import app.enums.Breed;

import java.util.Map;

public class DogPopulator {

    public static Map<Long, Dog> populate(DogDAO dogDAO){
        dogDAO.create(new Dog("King", Breed.LABRADOR ));
        dogDAO.create(new Dog("Mini", Breed.DACHSHUND ));
        dogDAO.create(new Dog("Boss", Breed.FRENCHIE ));
        dogDAO.create(new Dog("Osvald", Breed.BRETON ));
        return dogDAO.getDogs();
    }
}
