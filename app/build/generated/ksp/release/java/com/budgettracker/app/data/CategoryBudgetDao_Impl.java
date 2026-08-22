package com.budgettracker.app.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CategoryBudgetDao_Impl implements CategoryBudgetDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CategoryBudget> __insertionAdapterOfCategoryBudget;

  private final EntityDeletionOrUpdateAdapter<CategoryBudget> __deletionAdapterOfCategoryBudget;

  private final EntityDeletionOrUpdateAdapter<CategoryBudget> __updateAdapterOfCategoryBudget;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllCategories;

  public CategoryBudgetDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCategoryBudget = new EntityInsertionAdapter<CategoryBudget>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `category_budgets` (`id`,`title`,`limit`,`type`,`position`,`isExcluded`,`colorHex`,`iconName`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CategoryBudget entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindDouble(3, entity.getLimit());
        statement.bindString(4, entity.getType());
        statement.bindLong(5, entity.getPosition());
        final int _tmp = entity.isExcluded() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindString(7, entity.getColorHex());
        statement.bindString(8, entity.getIconName());
      }
    };
    this.__deletionAdapterOfCategoryBudget = new EntityDeletionOrUpdateAdapter<CategoryBudget>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `category_budgets` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CategoryBudget entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfCategoryBudget = new EntityDeletionOrUpdateAdapter<CategoryBudget>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `category_budgets` SET `id` = ?,`title` = ?,`limit` = ?,`type` = ?,`position` = ?,`isExcluded` = ?,`colorHex` = ?,`iconName` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CategoryBudget entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindDouble(3, entity.getLimit());
        statement.bindString(4, entity.getType());
        statement.bindLong(5, entity.getPosition());
        final int _tmp = entity.isExcluded() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindString(7, entity.getColorHex());
        statement.bindString(8, entity.getIconName());
        statement.bindLong(9, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAllCategories = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM category_budgets";
        return _query;
      }
    };
  }

  @Override
  public Object insertCategory(final CategoryBudget category,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfCategoryBudget.insertAndReturnId(category);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertCategories(final List<CategoryBudget> categories,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCategoryBudget.insert(categories);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteCategory(final CategoryBudget category,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfCategoryBudget.handle(category);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateCategory(final CategoryBudget category,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfCategoryBudget.handle(category);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateCategories(final List<CategoryBudget> categories,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfCategoryBudget.handleMultiple(categories);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllCategories(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllCategories.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAllCategories.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CategoryBudget>> getAllCategoriesFlow() {
    final String _sql = "SELECT * FROM category_budgets ORDER BY position ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"category_budgets"}, new Callable<List<CategoryBudget>>() {
      @Override
      @NonNull
      public List<CategoryBudget> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfLimit = CursorUtil.getColumnIndexOrThrow(_cursor, "limit");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "position");
          final int _cursorIndexOfIsExcluded = CursorUtil.getColumnIndexOrThrow(_cursor, "isExcluded");
          final int _cursorIndexOfColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "colorHex");
          final int _cursorIndexOfIconName = CursorUtil.getColumnIndexOrThrow(_cursor, "iconName");
          final List<CategoryBudget> _result = new ArrayList<CategoryBudget>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CategoryBudget _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final double _tmpLimit;
            _tmpLimit = _cursor.getDouble(_cursorIndexOfLimit);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final int _tmpPosition;
            _tmpPosition = _cursor.getInt(_cursorIndexOfPosition);
            final boolean _tmpIsExcluded;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsExcluded);
            _tmpIsExcluded = _tmp != 0;
            final String _tmpColorHex;
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex);
            final String _tmpIconName;
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName);
            _item = new CategoryBudget(_tmpId,_tmpTitle,_tmpLimit,_tmpType,_tmpPosition,_tmpIsExcluded,_tmpColorHex,_tmpIconName);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllCategoriesList(final Continuation<? super List<CategoryBudget>> $completion) {
    final String _sql = "SELECT * FROM category_budgets ORDER BY position ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CategoryBudget>>() {
      @Override
      @NonNull
      public List<CategoryBudget> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfLimit = CursorUtil.getColumnIndexOrThrow(_cursor, "limit");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "position");
          final int _cursorIndexOfIsExcluded = CursorUtil.getColumnIndexOrThrow(_cursor, "isExcluded");
          final int _cursorIndexOfColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "colorHex");
          final int _cursorIndexOfIconName = CursorUtil.getColumnIndexOrThrow(_cursor, "iconName");
          final List<CategoryBudget> _result = new ArrayList<CategoryBudget>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CategoryBudget _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final double _tmpLimit;
            _tmpLimit = _cursor.getDouble(_cursorIndexOfLimit);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final int _tmpPosition;
            _tmpPosition = _cursor.getInt(_cursorIndexOfPosition);
            final boolean _tmpIsExcluded;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsExcluded);
            _tmpIsExcluded = _tmp != 0;
            final String _tmpColorHex;
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex);
            final String _tmpIconName;
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName);
            _item = new CategoryBudget(_tmpId,_tmpTitle,_tmpLimit,_tmpType,_tmpPosition,_tmpIsExcluded,_tmpColorHex,_tmpIconName);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getCategoryById(final int id,
      final Continuation<? super CategoryBudget> $completion) {
    final String _sql = "SELECT * FROM category_budgets WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CategoryBudget>() {
      @Override
      @Nullable
      public CategoryBudget call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfLimit = CursorUtil.getColumnIndexOrThrow(_cursor, "limit");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "position");
          final int _cursorIndexOfIsExcluded = CursorUtil.getColumnIndexOrThrow(_cursor, "isExcluded");
          final int _cursorIndexOfColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "colorHex");
          final int _cursorIndexOfIconName = CursorUtil.getColumnIndexOrThrow(_cursor, "iconName");
          final CategoryBudget _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final double _tmpLimit;
            _tmpLimit = _cursor.getDouble(_cursorIndexOfLimit);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final int _tmpPosition;
            _tmpPosition = _cursor.getInt(_cursorIndexOfPosition);
            final boolean _tmpIsExcluded;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsExcluded);
            _tmpIsExcluded = _tmp != 0;
            final String _tmpColorHex;
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex);
            final String _tmpIconName;
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName);
            _result = new CategoryBudget(_tmpId,_tmpTitle,_tmpLimit,_tmpType,_tmpPosition,_tmpIsExcluded,_tmpColorHex,_tmpIconName);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
