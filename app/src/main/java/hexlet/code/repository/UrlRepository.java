package hexlet.code.repository;

import hexlet.code.dto.urls.UrlListItem;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlRepository extends BaseRepository {

    public static void save(Url url) throws SQLException {
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, url.getName());

            var createdAt = LocalDateTime.now();
            stmt.setTimestamp(2, Timestamp.valueOf(createdAt));

            stmt.executeUpdate();
            var generatedKeys = stmt.getGeneratedKeys();

            if (generatedKeys.next()) {
                url.setId(generatedKeys.getLong(1));
                url.setCreatedAt(createdAt);
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static List<Url> getEntities() throws SQLException {
        var sql = "SELECT * FROM urls ORDER by id DESC";  // ← ИЗМЕНЕНО: ASC → DESC
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            var result = new ArrayList<Url>();

            while (resultSet.next()) {
                var url = mapRowToUrl(resultSet);
                url.setUrlChecks(UrlCheckRepository.findByUrlId(url.getId()));
                result.add(url);
            }

            return result;
        }
    }

    public static Optional<Url> find(Long id) throws SQLException {
        var sql = "SELECT * FROM urls WHERE id = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            var resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                var url = mapRowToUrl(resultSet);
                url.setUrlChecks(UrlCheckRepository.findByUrlId(id));
                return Optional.of(url);
            }

            return Optional.empty();
        }
    }

    public static Optional<Url> findByName(String name) throws SQLException {
        var sql = "SELECT * FROM urls WHERE name = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                var url = mapRowToUrl(resultSet);
                url.setUrlChecks(UrlCheckRepository.findByUrlId(url.getId()));
                return Optional.of(url);
            }
            return Optional.empty();
        }
    }

    public static void removeAll() throws SQLException {
        String sql = "DELETE FROM urls";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }

    private static Url mapRowToUrl(ResultSet resultSet) throws SQLException {
        var id = resultSet.getLong("id");
        var name = resultSet.getString("name");
        var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();

        var url = new Url(name, createdAt);
        url.setId(id);
        url.setUrlChecks(new ArrayList<>());
        return url;
    }

    public static List<UrlListItem> getAllWithLastChecks() throws SQLException {
        System.out.println("=== getAllWithLastChecks() ===");
        List<Url> urls = getEntities();
        System.out.println("Total URLs: " + urls.size());

        List<UrlListItem> items = urls.stream()
                .map(url -> {
                    UrlCheck lastCheck = null;
                    try {
                        lastCheck = UrlCheckRepository.findLatestByUrlId(url.getId())
                                .orElse(null);
                        System.out.println("URL: " + url.getName() + " (ID: " + url.getId()
                                + ") -> LastCheck status: " + (lastCheck != null ? lastCheck.getStatusCode() : "null"));
                    } catch (SQLException e) {
                        System.err.println("Error getting last check for URL " + url.getId() + ": " + e.getMessage());
                    }
                    return UrlListItem.fromUrl(url, lastCheck);
                })
                .toList();

        System.out.println("Total items created: " + items.size());
        return items;
    }
}