package app.controllers;

import app.daos.DogDAO;
import app.entities.Dog;
import io.javalin.http.Context;

public class DogController {
    private final DogDAO dogDAO;

    public DogController(DogDAO dogDAO) {
        this.dogDAO = dogDAO;
    }

    public void getAll(Context ctx){
        ctx.json(dogDAO.getAll());
    }

    public void getById(Context ctx){
        long id = Long.parseLong(ctx.pathParam("id"));
        Dog dog = dogDAO.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dog with id " + id + " not found"));
        ctx.status(200)
                .json(dog);
    }

    public void create(Context ctx){
        Dog dog = ctx.bodyAsClass(Dog.class);
        dog = dogDAO.create(dog);
        ctx.json(dog);
    }

}
