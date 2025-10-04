package zhoma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zhoma.models.ItemPhoto;

public interface ItemPhotoRepository extends JpaRepository<ItemPhoto,Long> {
}
