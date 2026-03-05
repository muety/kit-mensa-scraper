import polars as pl
import glob
import os
from sklearn.cluster import KMeans
import numpy as np

# Replicate data loading
archive_dir = "../archive"
json_files = sorted(glob.glob(os.path.join(archive_dir, "*.json")))
# Use a subset for speed
json_files = json_files[:50]

dfs = []

target_schema = {
    "price": pl.Float64,
    "kcal": pl.Float64,
    "proteins": pl.Float64,
    "carbs": pl.Float64,
    "sugar": pl.Float64,
    "fat": pl.Float64,
    "saturated": pl.Float64,
    "salt": pl.Float64,
    "scoreCo2": pl.Int64,
    "scoreWater": pl.Int64,
    "scoreAnimals": pl.Int64,
    "scoreRainforest": pl.Int64,
    "co2Emissions": pl.Float64,
    "waterConsumption": pl.Float64,
    "name": pl.String,
    "line": pl.String,
    "type": pl.String,
    "additives": pl.List(pl.String),
}

expected_cols = [
    "name",
    "line",
    "price",
    "type",
    "kcal",
    "proteins",
    "carbs",
    "sugar",
    "fat",
    "saturated",
    "salt",
    "additives",
    "scoreCo2",
    "scoreWater",
    "scoreAnimals",
    "scoreRainforest",
    "co2Emissions",
    "waterConsumption",
]

for file in json_files:
    try:
        df = pl.read_json(file)
        if not df.is_empty() and df.width > 0:
            is_valid = True
            for col in df.columns:
                if col in target_schema:
                    current_dtype = df.schema[col]
                    target_dtype = target_schema[col]
                    if current_dtype == target_dtype:
                        continue
                    if current_dtype == pl.Null:
                        continue
                    if target_dtype in [pl.Float64, pl.Int64] and current_dtype in [
                        pl.Float64,
                        pl.Int64,
                    ]:
                        continue
                    is_valid = False
                    break

            if not is_valid:
                continue

            for col in expected_cols:
                target_dtype = target_schema.get(col, pl.String)
                if col not in df.columns:
                    df = df.with_columns(pl.lit(None, dtype=target_dtype).alias(col))
                else:
                    if df.schema[col] != target_dtype:
                        df = df.with_columns(pl.col(col).cast(target_dtype))

            df = df.select(expected_cols)
            dfs.append(df)
    except:
        pass

if dfs:
    combined_df = pl.concat(dfs)
    combined_df = combined_df.unique(subset=["name"])

    # Filter valid kcal/proteins
    combined_df = combined_df.filter((pl.col("kcal") > 0) & (pl.col("proteins") > 0))
    combined_df = combined_df.filter(pl.col("kcal") < 5000)

    print(f"Data shape: {combined_df.shape}")

    # Clustering logic
    X = combined_df.select(["kcal", "proteins"]).to_numpy()
    n_clusters = 10  # reduced for small dataset

    if len(X) > n_clusters:
        kmeans = KMeans(n_clusters=n_clusters, random_state=42, n_init=10)
        kmeans.fit(X)

        closest_indices = []
        for center in kmeans.cluster_centers_:
            distances = np.linalg.norm(X - center, axis=1)
            closest_idx = np.argmin(distances)
            closest_indices.append(closest_idx)

        representatives = combined_df[closest_indices]
        print(f"Representatives shape: {representatives.shape}")
        print(representatives.select(["name", "kcal", "proteins"]))
    else:
        print("Not enough data for clustering")
else:
    print("No data")
