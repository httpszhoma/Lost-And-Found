package zhoma.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zhoma.dto.ItemDto;
import zhoma.mappers.ItemMapper;
import zhoma.models.Item;
import zhoma.services.ItemService;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.List;

@RequestMapping("/items")
@RestController
@CrossOrigin("*")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    public ItemController(ItemService itemService, ItemMapper itemMapper) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }


    @PostMapping("/create")
    public ResponseEntity<Item> createItem(
            @RequestPart("item") ItemDto itemDto,
            @RequestPart("photos") List<MultipartFile> photos
    ) throws IOException {
        return ResponseEntity.ok(itemService.create(itemMapper.toItem(itemDto), photos));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(
            @PathVariable Long id,
            @RequestBody ItemDto itemDto
    ) {
        return ResponseEntity.ok(itemService.update(id, itemMapper.toItem(itemDto)));
    }


    @GetMapping
    public ResponseEntity<Page<Item>> getAllItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(itemService.getAll(pageRequest));
    }

}
