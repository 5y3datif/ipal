import numpy as np
import cv2
import os
from retinaface import RetinaFace
from arcface import ArcFace
import faiss

Train_path = './Images/Train'
TrainimgList = np.sort(os.listdir(Train_path))
Test_path = './Images/Test'
TestimgList = np.sort(os.listdir(Test_path))
print(TrainimgList)

det = RetinaFace(quality='normal')
model = ArcFace.ArcFace()
d = 512
index = faiss.IndexFlatL2(d)



class Image_Data():
	"""imagesstring for Image_Data"""
	def __init__(self):
		
		self.images =  []
		self.faces = []
		self.embeddings = []
		self.PersonNames = []
		self.imageDB = self.ImageDatabase(TrainimgList)
		self.faceDB = self.DetectFace(self.images)
		self.embDB = self.ExtractEmbedding(self.faces)
		
	def ImageDatabase(self, TrainimgList):
		for pname in TrainimgList:
			# print(pname)
			curImage = cv2.imread(f'{Train_path}/{pname}')
			self.images.append(curImage)
			self.PersonNames.append(os.path.splitext(pname)[0])
		return self.images, self.PersonNames
	def DetectFace(self, images):
		count = 1
		for img in images:
			face = det.predict(img)
			fa = img[face[0]['y1']:face[0]['y2'], face[0]['x1']:face[0]['x2']]
			cv2.imwrite(f'Face{count}.jpg', fa)
			count = count +1
			self.faces.append(img[face[0]['y1']:face[0]['y2'], face[0]['x1']:face[0]['x2']])
		return self.faces

	def ExtractEmbedding(self, faces):
		count = 1
		for face in faces:
			# cv2.imshow('Face', face)
			# cv2.waitKey(0)
			emb = model.calc_emb(face)
			emb = np.reshape(emb, (1, 512))
			self.embeddings.append(emb)
			# index.add(emb)
		# faiss.write_index(index,"vector.index")
		return self.embeddings


	def Detect_Face(self, img):
		face = det.predict(img)
		x1,y1,x2, y2 = face[0]['x1'], face[0]['y1'], face[0]['x2'],face[0]['y2']
		return x1,y1,x2,y2

	def Calc_Embedding(self, face):
		emb = model.calc_emb(face)
		emb = np.reshape(emb, (1, 512))
		return emb

obj = Image_Data()
obj.imageDB
obj.faceDB
em = obj.embDB
names = obj.PersonNames
print(names)
k = 3

index2 = faiss.read_index("vector.index")
for name in TestimgList:
	im = cv2.imread(f'{Test_path}/{name}')
	# im = cv2.resize(im, (200,200))
	# cv2.imshow('Image', im)
	# cv2.waitKey(0)
	x1,y1,x2,y2 = obj.Detect_Face(im)
	image = cv2.rectangle(im, (x1,y1), (x2,y2), (255,0,0), 2)
	face = im[y1:y2, x1:x2]
	embed = obj.Calc_Embedding(face)
	distances, neighbors = index2.search(embed, k)
	print(neighbors)
	name = names[neighbors[0][0]].upper()
	# name = 'Unknown'
	cv2.putText(im, name, (x1+6, y2-6), cv2.FONT_HERSHEY_COMPLEX, 0.5, (0,255,0), 2)
	cv2.imshow('Image', im)
	cv2.waitKey(0)