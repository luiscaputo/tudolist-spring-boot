package ao.caputo.tudolist.tasks;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ITaskRepository extends  JpaRepository<TaskModel, UUID> {
  List<TaskModel> findTasksByUserId(UUID id);
  TaskModel findTaskById(UUID id);
}
