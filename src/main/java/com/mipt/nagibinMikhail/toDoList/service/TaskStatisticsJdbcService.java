package com.mipt.nagibinMikhail.toDoList.service;

import com.mipt.nagibinMikhail.toDoList.dto.PriorityStatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskStatisticsJdbcService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Получение статистики количества задач по приоритетам
     * Использует JdbcTemplate и RowMapper
     */
    public List<PriorityStatsDto> getTasksCountByPriority() {
        String sql = """
            SELECT 
                COALESCE(priority, 'NO_PRIORITY') as priority,
                COUNT(*) as count
            FROM tasks
            GROUP BY priority
            ORDER BY 
                CASE priority
                    WHEN 'HIGH' THEN 1
                    WHEN 'MEDIUM' THEN 2
                    WHEN 'LOW' THEN 3
                    ELSE 4
                END
            """;

        return jdbcTemplate.query(sql, new PriorityStatsRowMapper());
    }

    /**
     * Альтернативная реализация с использованием RowMapper как лямбды
     */
    public List<PriorityStatsDto> getTasksCountByPriorityLambda() {
        String sql = """
            SELECT 
                COALESCE(priority, 'NO_PRIORITY') as priority,
                COUNT(*) as count
            FROM tasks
            GROUP BY priority
            """;

        RowMapper<PriorityStatsDto> mapper = (rs, rowNum) ->
            new PriorityStatsDto(rs.getString("priority"), rs.getLong("count"));

        return jdbcTemplate.query(sql, mapper);
    }

    /**
     * Статистика по статусу выполнения
     */
    public Map<String, Long> getCompletionStats() {
        String sql = """
            SELECT 
                CASE WHEN completed THEN 'completed' ELSE 'active' END as status,
                COUNT(*) as count
            FROM tasks
            GROUP BY completed
            """;

        return jdbcTemplate.query(sql, rs -> {
            Map<String, Long> result = new java.util.HashMap<>();
            while (rs.next()) {
                result.put(rs.getString("status"), rs.getLong("count"));
            }
            return result;
        });
    }

    /**
     * Общая статистика по задачам (одним запросом)
     */
    public String getFullStatistics() {
        String sql = """
            SELECT 
                COUNT(*) as total,
                SUM(CASE WHEN completed THEN 1 ELSE 0 END) as completed,
                SUM(CASE WHEN NOT completed THEN 1 ELSE 0 END) as active,
                COUNT(DISTINCT priority) as distinct_priorities,
                COUNT(CASE WHEN due_date < CURRENT_DATE AND NOT completed THEN 1 END) as overdue
            FROM tasks
            """;

        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                return String.format("""
                    ========== TASK STATISTICS ==========
                    Total tasks: %d
                    Completed: %d
                    Active: %d
                    Distinct priorities: %d
                    Overdue tasks: %d
                    ======================================
                    """,
                    rs.getLong("total"),
                    rs.getLong("completed"),
                    rs.getLong("active"),
                    rs.getLong("distinct_priorities"),
                    rs.getLong("overdue")
                );
            }
            return "No statistics available";
        });
    }

    // Custom RowMapper implementation
    private static class PriorityStatsRowMapper implements RowMapper<PriorityStatsDto> {
        @Override
        public PriorityStatsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new PriorityStatsDto(
                rs.getString("priority"),
                rs.getLong("count")
            );
        }
    }
}