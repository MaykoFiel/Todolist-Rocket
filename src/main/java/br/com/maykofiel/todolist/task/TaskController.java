package br.com.maykofiel.todolist.task;

import br.com.maykofiel.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("")
    public ResponseEntity creat(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);

        var currentDate = LocalDateTime.now();
        System.out.println("--- DEBUG DE DATAS ---");
        System.out.println("Data do Sistema: " + currentDate);
        System.out.println("Data de Início (JSON): " + taskModel.getStartAt());
        System.out.println("Data de Término (JSON): " + taskModel.getEndAt());
        System.out.println("-----------------------");

        // Validação do Início
        if (currentDate.isAfter(taskModel.getStartAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data de início deve ser maior que a data atual.");
        }

        // Validação do Término
        if (currentDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data de término deve ser maior que a data atual.");
        }
        var tasks = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @GetMapping("")
    public List<TaskModel> list(HttpServletRequest request) {

        var idUser = request.getAttribute("idUser");
        var tasks = this.taskRepository.findByIdUser((UUID) idUser);
        return tasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {

        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Tarefa não encontrada");
        }
        var idUser = request.getAttribute("idUser");
        if (!task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Usuário não tem permissão para alterar esta tarefa");
        }

        Utils.copyNullProperties(taskModel, task);
        var taskUpdated = this .taskRepository.save(task);
        return ResponseEntity.ok().body(taskUpdated);

    }

}
