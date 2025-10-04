package zhoma.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zhoma.models.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item,Long> {


    List<Item> findByCategoryIgnoreCase(String category);


}
