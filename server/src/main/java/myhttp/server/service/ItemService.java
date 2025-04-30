// server/src/main/java/myhttp/server/service/ItemService.java
package myhttp.server.service;

import myhttp.server.entity.Item;
import myhttp.server.repository.ItemRepository;
import java.util.List;

public class ItemService {
    private final ItemRepository repo;

    public ItemService(ItemRepository repo) {
        this.repo = repo;
    }

    public Item getItem(Long id) {
        return repo.find(id);
    }

    public List<Item> getAllItems() {
        return repo.findAll();
    }

    public Item createItem(Item item) {
        return repo.save(item);
    }

    public Item updateItem(Item item) {
        return repo.update(item);
    }

    public void deleteItem(Long id) {
        repo.delete(id);
    }
}