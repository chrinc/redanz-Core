package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.OutText;
import ch.redanz.redanzCore.model.workshop.OutTextId;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OutTextRepo extends JpaRepository<OutText, OutTextId> {

}
