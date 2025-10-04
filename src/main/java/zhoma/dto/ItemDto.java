package zhoma.dto;

import org.springframework.web.multipart.MultipartFile;
import zhoma.models.Item;

import java.util.List;

public record ItemDto(
        String name,
        String description,
        String category,
        Item.Status status
        // LOST or FOUND
) { }