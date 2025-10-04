package zhoma.mappers;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import zhoma.dto.ItemDto;
import zhoma.models.Item;


@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item toItem(ItemDto itemDto);
    ItemDto toItemDto(Item item);
}
