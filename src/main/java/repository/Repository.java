package repository;

import java.sql.SQLException;
import java.util.List;

// TODO add all methods
public interface Repository<T> {

    List<T> getAll() throws SQLException ;

}
