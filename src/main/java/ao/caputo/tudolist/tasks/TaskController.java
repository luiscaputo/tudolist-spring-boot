package ao.caputo.tudolist.tasks;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ao.caputo.tudolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping("/")
  public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    try {
      var userId = request.getAttribute("userId");
      taskModel.setUserId((UUID) userId);

      var currentDate = LocalDateTime.now();

      if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("A data de inicio/termino deve ser maior que a data atual");
      }

      if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("A data de inicio não pode ser posterior a data de termino");
      }

      return ResponseEntity.ok(this.taskRepository.save(taskModel));

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
  }

  @GetMapping("/")
  public List<TaskModel> list(HttpServletRequest request) {
    var userId = request.getAttribute("userId");
    return this.taskRepository.findTasksByUserId((UUID) userId);
  }

  @PutMapping("/{id}")
  public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
    try {
      var task = this.taskRepository.findById(id).orElse(null);
    
      if(task == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada");
      }
      var userId = request.getAttribute("userId");
  
      if(!task.getUserId().equals(userId)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não tem permissão para alterar essa tarefa");
      }
  
      Utils.copyNonNullProperty(taskModel, task);
  
      var taskUpdated = this.taskRepository.save(task);
      return ResponseEntity.ok().body(taskUpdated);
    }catch(Exception e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }
}
