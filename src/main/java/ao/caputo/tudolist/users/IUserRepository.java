package ao.caputo.tudolist.users;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface  IUserRepository extends  JpaRepository<UserModel, UUID>{
  UserModel findUserByUsername(String username);
}
