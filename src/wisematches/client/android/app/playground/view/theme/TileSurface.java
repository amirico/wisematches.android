package wisematches.client.android.app.playground.view.theme;

import android.content.res.Resources;
import android.graphics.*;
import wisematches.client.android.R;
import wisematches.client.android.data.model.scribble.ScribbleTile;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class TileSurface {
	private final float tileSize = DEFAULT_TILE_SIZE;

	private final Bitmap[] tilesSelected = new Bitmap[11];
	private final Bitmap[] tilesUnselected = new Bitmap[11];
	private final Bitmap[] tilesPinnedSelected = new Bitmap[11];
	private final Bitmap[] tilesPinnedUnselected = new Bitmap[11];
	private final Bitmap[] tilesHighlighters = new Bitmap[11];

	private final Matrix matrix = new Matrix();

	private static final int DEFAULT_TILE_SIZE = 22;
	private static final float TEXT_SCALE_FACTOR = 0.7f;

	private final Paint textPaint = new Paint();
	private final Paint bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);

	public TileSurface(Resources resources) {
		final Bitmap bitmap = android.graphics.BitmapFactory.decodeResource(resources, R.drawable.board_tiles);
		final Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, DEFAULT_TILE_SIZE * 11, DEFAULT_TILE_SIZE * 5, true);
		for (int i = 0; i < 11; i++) {
			tilesHighlighters[i] = getScaledBitmap(scaledBitmap, 4, i);
			tilesSelected[i] = getScaledBitmap(scaledBitmap, 1, i);
			tilesUnselected[i] = getScaledBitmap(scaledBitmap, 0, i);
			tilesPinnedSelected[i] = getScaledBitmap(scaledBitmap, 3, i);
			tilesPinnedUnselected[i] = getScaledBitmap(scaledBitmap, 2, i);
		}

		textPaint.setTextAlign(Paint.Align.CENTER);
		textPaint.setAntiAlias(true);

		bitmapPaint.setDither(true);
		bitmapPaint.setAntiAlias(true);
		bitmapPaint.setFilterBitmap(true);
	}

	private Bitmap getScaledBitmap(Bitmap scaledBitmap, int row, int col) {
		return Bitmap.createBitmap(scaledBitmap, DEFAULT_TILE_SIZE * col, DEFAULT_TILE_SIZE * row, DEFAULT_TILE_SIZE, DEFAULT_TILE_SIZE);
	}

	public void drawHighlighter(Canvas canvas, ScribbleTile tile, int x, int y, int scale) {
		canvas.drawBitmap(tilesHighlighters[tile.getCost()], matrix(x, y, scale / tileSize), bitmapPaint);
	}

	public void drawScribbleTile(Canvas canvas, ScribbleTile tile, int x, int y, int scale, boolean selected, boolean pinned) {
		Bitmap bitmap;
		if (selected && pinned) {
			bitmap = tilesPinnedSelected[tile.getCost()];
		} else if (selected) {
			bitmap = tilesSelected[tile.getCost()];
		} else if (pinned) {
			bitmap = tilesPinnedUnselected[tile.getCost()];
		} else {
			bitmap = tilesUnselected[tile.getCost()];
		}

		final float sx = scale / tileSize;
		canvas.drawBitmap(bitmap, matrix(x, y, sx), bitmapPaint);

		if (tile.isWildcard()) {
			textPaint.setColor(Color.BLACK);
		} else {
			textPaint.setColor(Color.WHITE);
		}
		textPaint.setTextSize(scale * sx * TEXT_SCALE_FACTOR);
		textPaint.setFakeBoldText(selected);

		canvas.drawText(tile.getLetter(), x + scale / 2 + TEXT_SCALE_FACTOR, y + (scale - textPaint.ascent()) / 2 + TEXT_SCALE_FACTOR, textPaint);
	}

	private Matrix matrix(int x, int y, float s) {
		matrix.reset();
		matrix.postScale(s, s);
		matrix.postTranslate(x + s, y + s);
		return matrix;
	}
}
