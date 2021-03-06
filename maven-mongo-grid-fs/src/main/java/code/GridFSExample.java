package code;

import static com.mongodb.client.model.Filters.eq;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;

public class GridFSExample {
    public static void main(String[] args) {
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);

        GridFSExample gridFs = new GridFSExample();
        ObjectId darkObjectId = gridFs.upload("maven-mongo-grid-fs/darkness.jpg", "darkness");
        ObjectId lightObjectId = gridFs.upload("maven-mongo-grid-fs/lightness.jpg", "lightness");
        gridFs.findAll();
        gridFs.find(lightObjectId);
        gridFs.download("darkness", "maven-mongo-grid-fs/new-darkness.jpg");
        gridFs.rename(darkObjectId, "re-darkness");
        gridFs.delete(darkObjectId);
        gridFs.findAll();
    }

    // Upload File
    public ObjectId upload(String filePath, String fileName) {
        System.out.println("Calling upload...");
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        ObjectId fileId = null;
        try {
            MongoDatabase database = mongoClient.getDatabase("MavenMongoGridFs");
            GridFSBucket gridBucket = GridFSBuckets.create(database);
            InputStream inputStream = new FileInputStream(new File(filePath));
            // Create some custom options
            GridFSUploadOptions uploadOptions = new GridFSUploadOptions()
                    .chunkSizeBytes(1024)
                    .metadata(new Document("type", "image")
                            .append("upload_date", format.parse("2016-09-01T00:00:00Z"))
                            .append("content_type", "image/jpg"));
            fileId = gridBucket.uploadFromStream(fileName, inputStream, uploadOptions);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }

        return fileId;
    }


    // Find All
    public void findAll() {
        System.out.println("Calling findAll...");
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));

        try {
            MongoDatabase database = mongoClient.getDatabase("MavenMongoGridFs");
            GridFSBucket gridBucket = GridFSBuckets.create(database);

            gridBucket.find().forEach((Block<GridFSFile>) gridFSFile -> {
                System.out.println("File Name:- " + gridFSFile.getFilename());
                System.out.println("Meta Data:- " + gridFSFile.getMetadata());
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
    }

    // Find by Id
    public void find(ObjectId objectId) {
        System.out.println("Calling find...");
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));

        try {
            MongoDatabase database = mongoClient.getDatabase("MavenMongoGridFs");
            GridFSBucket gridBucket = GridFSBuckets.create(database);

            GridFSFile gridFSFile = gridBucket.find(eq("_id", objectId)).first();
            System.out.println("File Name:- " + gridFSFile.getFilename());
            System.out.println("Meta Data:- " + gridFSFile.getMetadata());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
    }

    // Download File
    public void download(String fileName, String filePath) {
        System.out.println("Calling download...");
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));

        try {
            MongoDatabase database = mongoClient.getDatabase("MavenMongoGridFs");
            GridFSBucket gridBucket = GridFSBuckets.create(database);

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            gridBucket.downloadToStream(fileName, fileOutputStream);
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
    }

    //Rename File
    public void rename(ObjectId objectId, String newFileName) {
        System.out.println("Calling rename...");
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        try {
            MongoDatabase database = mongoClient.getDatabase("MavenMongoGridFs");
            GridFSBucket gridBucket = GridFSBuckets.create(database);
            gridBucket.rename(objectId, newFileName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
    }

    //Delete File
    public void delete(ObjectId objectId) {
        System.out.println("Calling delete...");
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        try {
            MongoDatabase database = mongoClient.getDatabase("MavenMongoGridFs");
            GridFSBucket gridBucket = GridFSBuckets.create(database);
            gridBucket.delete(objectId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
    }
}