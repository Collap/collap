package io.collap.std.post.util;

import io.collap.std.post.entity.Category;
import io.collap.util.ParseUtils;
import org.hibernate.Session;

public class CategoryUtil {

    // TODO: Find a solution with less duplicate code.

    public static Category getCategoryFromDatabase (Session session, String remainingPath) {
        return getCategoryFromDatabaseOrCreate (session, remainingPath, false);
    }

    public static Category getCategoryFromDatabaseOrCreate (Session session, String remainingPath, boolean create) {
        Long id = ParseUtils.parseLong (remainingPath);
        Category category;
        if (id == null) { /* Category not found. */
            if (create) {
                category = Category.createTransientCategory ();
            }else {
                category = null;
            }
        }else {
            category = (Category) session.get (Category.class, id);
        }

        return category;
    }

}
