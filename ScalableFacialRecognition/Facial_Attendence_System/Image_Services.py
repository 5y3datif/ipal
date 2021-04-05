import numpy as np
import cv2
import os
from retinaface import RetinaFace
from arcface import ArcFace
import faiss

det = RetinaFace(quality='normal')
model = ArcFace.ArcFace()
d = 512
index = faiss.IndexFlatL2(d)
path = './KIETEmpTrain/'
imgList = np.sort(os.listdir(path))


class ImageServices():
    def __init__(self):
        self.images = []
        self.faces = []
        self.embeddings = []
        self.PersonNames = []
        self.imageDB = self.ImageDatabase(imgList)
        self.faceDB = self.DetectFace(self.images)
        self.embDB = self.ExtractEmbedding(self.faces)

    def ImageDatabase(self, imgList):
        for pname in imgList:
            # print(pname)
            curImage = cv2.imread(f'{path}/{pname}')
            self.images.append(curImage)
            self.PersonNames.append(os.path.splitext(pname)[0])
        return self.images, self.PersonNames

    def DetectFace(self, images):
        for img in images:
            face = det.predict(img)
            self.faces.append(img[face[0]['y1']:face[0]['y2'], face[0]['x1']:face[0]['x2']])
        return self.faces

    def ExtractEmbedding(self, faces):
        for face in faces:
            # cv2.imshow('Face', face)
            # cv2.waitKey(0)
            emb = model.calc_emb(face)
            emb = np.reshape(emb, (1, 512))
            self.embeddings.append(emb)
            index.add(emb)
        # faiss.write_index(index,"vector.index")
        return self.embeddings

    def FaceDetection(self, img):
        face = det.predict(img)
        x1, y1, x2, y2 = face[0]['x1'], face[0]['y1'], face[0]['x2'], face[0]['y2']
        return x1, y1, x2, y2

    def Calc_Embedding(self, face):
        emb = model.calc_emb(face)
        emb = np.reshape(emb, (1, d))
        return emb

    def Recognize(self, emb, k):
        index2 = faiss.read_index('vector.index')
        distances, neighbors = index2.search(emb, k)
        name = self.PersonNames[neighbors[0][0]]
        return name

    def FaceFfrontalize(self, img):
        pass

    def DetectLivelines(self, img):
        pass

