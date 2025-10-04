package zhoma.services;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import zhoma.models.Item;
import zhoma.models.ItemPhoto;
import zhoma.repository.ItemPhotoRepository;
import zhoma.repository.ItemRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemPhotoRepository itemPhotoRepository;
    private final S3Service s3Service;


    @Transactional
    public Item create(Item item, List<MultipartFile> photos) throws IOException {
        // 1️⃣ Сначала сохраняем сам Item (без фото)
        Item savedItem = itemRepository.save(item);

        // 2️⃣ Теперь загружаем фото и связываем их с Item
        List<ItemPhoto> itemPhotos = new ArrayList<>();
        for (MultipartFile file : photos) {
            String url = s3Service.upload(file); // загружаем в S3
            ItemPhoto photo = new ItemPhoto();
            photo.setPhotoUrl(url);
            photo.setItem(savedItem);
            itemPhotoRepository.save(photo);
            itemPhotos.add(photo);
        }

        // 3️⃣ Присваиваем фотографии айтему и возвращаем
        savedItem.setPhotos(itemPhotos);

        return savedItem;
    }

    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    public Item update(Long id, Item updated) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setName(updated.getName());
        item.setDescription(updated.getDescription());
        item.setCategory(updated.getCategory());
        return itemRepository.save(item);
    }

    public Page<Item> getAll(PageRequest pageRequest) {
        return itemRepository.findAll(pageRequest);
    }

    public List<Item> getByCategory(String category) {
        return itemRepository.findByCategoryIgnoreCase(category);
    }


}
