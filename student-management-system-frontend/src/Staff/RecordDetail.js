import React, {useEffect, useState} from 'react';
import {
  Button,
  Card,
  Col,
  DatePicker,
  Descriptions,
  Form,
  InputNumber,
  Layout,
  message,
  Modal,
  Popover,
  Progress,
  Row,
  Select,
  Table,
  Upload
} from 'antd';
import {
  DeleteOutlined,
  DownloadOutlined,
  EditOutlined,
  PlusOutlined,
  ReloadOutlined,
  UploadOutlined
} from '@ant-design/icons';
import './Contents.css';
import api from "../api";
import moment from 'moment';

const {Content} = Layout;
const {Option} = Select;

const calculateUKGrade = (score) => {
  if (score >= 70) return 'A';
  if (score >= 60) return 'B';
  if (score >= 50) return 'C';
  if (score >= 40) return 'D';
  return 'F';
};

const RecordDetail = ({ recordId, setSelectedKey, onStudentClick }) => {
  const [record, setRecord] = useState(null);
  const [loading, setLoading] = useState(false);
  const [registrations, setRegistrations] = useState([]);
  const [newRegistration, setNewRegistration] = useState({
    studentId: null,
    score: null,
    isNew: true
  });
  const [students, setStudents] = useState([]);
  const [submitting, setSubmitting] = useState(false);
  const [editingKey, setEditingKey] = useState('');
  const [showAddRow, setShowAddRow] = useState(false);
  const [isDeleteModalVisible, setIsDeleteModalVisible] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [editForm] = Form.useForm();

  const fetchRecordData = async () => {
    if (!recordId) return;
    setLoading(true);
    try {
      // Fetch record details
      const response = await api.get(`/api/record/${recordId}`);
      if (response.data?.code === 200) {
        const recordData = response.data.data;
        setRecord({
          moduleCode: recordData.moduleCode,
          moduleName: recordData.moduleName,
          date: recordData.date,
          averageScore: recordData.averageScore,
          numberOfCandidates: recordData.numberOfCandidates,
          passRate: recordData.passRate
        });
      }

      // Fetch registrations for this record using the correct endpoint
      const registrationsResponse = await api.get('/api/registration', {
        params: {
          current: 1,
          size: 100,
          studentId: '',
          recordId: recordId
        }
      });

      if (registrationsResponse.data?.code === 200) {
        setRegistrations(registrationsResponse.data.data.records || []);
      }
    } catch (error) {
      message.error('Failed to fetch record data');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const fetchStudents = async () => {
    try {
      const response = await api.get('/api/student/list-students', {
        params: {
          current: 1,
          size: 100,
          id: '',
          firstName: '',
          lastName: '',
          username: '',
          programOfStudy: ''
        }
      });
      setStudents(response.data?.data?.records || []);
    } catch (error) {
      console.error('Error fetching students:', error);
      message.error('Failed to fetch students');
    }
  };

  useEffect(() => {
    fetchRecordData();
    fetchStudents();
  }, [recordId]);

  const handleDelete = async () => {
    try {
      await api.delete(`/api/record/${recordId}`);
      message.success('Record deleted successfully');
      setIsDeleteModalVisible(false);
      setSelectedKey('1'); // Navigate back to grade records
    } catch (error) {
      message.error('Failed to delete record');
      console.error(error);
    }
  };

  const handleAddRegistration = async () => {
    if (!newRegistration.studentId || newRegistration.score === null) {
      message.error('Please select a student and enter a score');
      return;
    }

    setSubmitting(true);
    try {
      const response = await api.post('/api/registration/add', {
        studentId: parseInt(newRegistration.studentId),
        recordId: parseInt(recordId),
        score: parseInt(newRegistration.score)
      });

      if (response.data?.code === 200) {
        message.success('Registration added successfully');
        setNewRegistration({ studentId: null, score: null, isNew: true });
        setShowAddRow(false);
        // Refresh both record data and registrations
        await fetchRecordData();
      } else {
        throw new Error(response.data?.message || 'Failed to add registration');
      }
    } catch (error) {
      console.error('Error adding registration:', error);
      message.error('Failed to add registration: ' + (error.message || 'Unknown error'));
    } finally {
      setSubmitting(false);
    }
  };

  const handleSaveEdit = async () => {
    try {
      setSubmitting(true);

      // Create an array of promises for all registration updates
      const updatePromises = registrations.map(registration => {
        return api.put(`/api/registration/edit/${registration.id}`, {
          studentId: registration.studentId,
          recordId: parseInt(recordId),
          score: parseInt(registration.score)
        });
      });

      // Wait for all updates to complete
      await Promise.all(updatePromises);

      message.success('All scores updated successfully');
      setEditingKey('');
      await fetchRecordData(); // Refresh the data
    } catch (error) {
      console.error('Error updating registrations:', error);
      message.error('Failed to update scores: ' + (error.message || 'Unknown error'));
    } finally {
      setSubmitting(false);
    }
  };

  // Add statistics calculation
  const calculateStatistics = (registrations) => {
    if (!registrations || registrations.length === 0) {
      return {
        totalStudents: 0,
        averageScore: 0,
        passRate: 0,
        gradeDistribution: {
          A: 0, B: 0, C: 0, D: 0, F: 0
        },
        highestScore: 0,
        lowestScore: 0,
        medianScore: 0,
        standardDeviation: 0
      };
    }

    const scores = registrations.map(r => r.score);
    const passCount = registrations.filter(r => r.score >= 40).length;

    // Calculate grade distribution
    const gradeDistribution = registrations.reduce((acc, reg) => {
      const grade = calculateUKGrade(reg.score);
      acc[grade] = (acc[grade] || 0) + 1;
      return acc;
    }, { A: 0, B: 0, C: 0, D: 0, F: 0 });

    // Calculate median
    const sortedScores = [...scores].sort((a, b) => a - b);
    const median = sortedScores.length % 2 === 0
      ? (sortedScores[sortedScores.length / 2 - 1] + sortedScores[sortedScores.length / 2]) / 2
      : sortedScores[Math.floor(sortedScores.length / 2)];

    // Calculate standard deviation
    const mean = scores.reduce((a, b) => a + b, 0) / scores.length;
    const standardDeviation = Math.sqrt(
      scores.reduce((sq, n) => sq + Math.pow(n - mean, 2), 0) / scores.length
    );

    return {
      totalStudents: registrations.length,
      averageScore: (scores.reduce((a, b) => a + b, 0) / registrations.length).toFixed(2),
      passRate: ((passCount / registrations.length) * 100).toFixed(1),
      gradeDistribution,
      highestScore: Math.max(...scores),
      lowestScore: Math.min(...scores),
      medianScore: median.toFixed(2),
      standardDeviation: standardDeviation.toFixed(2)
    };
  };

  // Add delete registration handler
  const handleDeleteRegistration = async (registrationId) => {
    try {
      await api.delete(`/api/registration/${registrationId}`);
      message.success('Registration deleted successfully');
      await fetchRecordData(); // Refresh data after deletion
    } catch (error) {
      console.error('Error deleting registration:', error);
      message.error('Failed to delete registration');
    }
  };

  // Add edit record handler
  const handleEditRecord = async () => {
    try {
      const values = await editForm.validateFields();
      setSubmitting(true);

      const response = await api.put(`/api/record/edit/${recordId}`, {
        moduleCode: values.moduleCode,
        date: values.date.format('YYYY-MM-DD')
      });

      if (response.data?.code === 200) {
        message.success('Record updated successfully');
        setIsEditModalOpen(false);
        await fetchRecordData();
      } else {
        throw new Error(response.data?.message || 'Failed to update record');
      }
    } catch (error) {
      console.error('Error updating record:', error);
      message.error(error.response?.data?.message || 'Failed to update record');
    } finally {
      setSubmitting(false);
    }
  };

  const handleFileUpload = (info) => {
    const token = localStorage.getItem('accessToken');
    const file = info.file;

    if (!file) {
      message.error('No file selected for upload');
      return;
    }

    const formData = new FormData();
    formData.append('file', file);
    formData.append('recordId', recordId); // Add recordId to form data

    api.post('/api/registration/import-registrations', formData, {
      headers: {
        Authorization: `${token}`,
        'Content-Type': 'multipart/form-data',
      },
    })
    .then(() => {
      message.success('Registrations imported successfully');
      fetchRecordData(); // Refresh the data after import
    })
    .catch((error) => {
      console.error('Error importing registrations:', error);
      message.error('Failed to import registrations');
    });
  };

  const csvTemplateContent = (
      <div style={{maxWidth: '300px'}}>
        <p>Please download and follow the CSV template format for importing registrations:</p>
        <a
            href="./files/RegistrationDataTemplate.csv"
            download="Registration Data Template.csv"
            style={{
              color: '#1890ff',
              textDecoration: 'underline',
              display: 'flex',
              alignItems: 'center',
              gap: '8px'
            }}
        >
          <DownloadOutlined/> Download Template
      </a>
      <p style={{ marginTop: '8px', fontSize: '12px', color: '#666' }}>
        Note: The CSV file must include:
        <ul style={{ marginTop: '4px', paddingLeft: '16px' }}>
          <li>studentId: Valid student ID</li>
          <li>recordId: Valid record ID</li>
          <li>score: Number between 0-100</li>
        </ul>
      </p>
    </div>
  );

  return (
    <Content style={{ margin: '24px 16px 0', padding: 24, background: '#fff' }}>
      {/* Header Section */}
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>Record Detail</h2>
        <div style={{display: 'flex', gap: '8px'}}>
          <Button icon={<ReloadOutlined/>} onClick={fetchRecordData} loading={loading}/>
          <Button
              type="primary"
              icon={<EditOutlined/>}
              onClick={() => {
                editForm.setFieldsValue({
                  moduleCode: record?.moduleCode,
                  date: moment(record?.date)
                });
                setIsEditModalOpen(true);
              }}
          >
            Edit Record
          </Button>
          <Button
              danger
              icon={<DeleteOutlined/>}
              onClick={() => setIsDeleteModalVisible(true)}
          >
            Delete Record
          </Button>
        </div>
      </div>

      {/* Record Information Section */}
      <Descriptions bordered column={2} style={{ marginBottom: 24 }}>
        <Descriptions.Item label="Module Code">{record?.moduleCode}</Descriptions.Item>
        <Descriptions.Item label="Module Name">{record?.moduleName}</Descriptions.Item>
        <Descriptions.Item label="Exam Date">{record?.date}</Descriptions.Item>
        <Descriptions.Item label="Number of Candidates">{record?.numberOfCandidates || 0}</Descriptions.Item>
      </Descriptions>

      {/* Enhanced Statistics Section */}
      {registrations.length > 0 && (
          <Card
              title={
                <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                  <span>Assessment Overview</span>
                  <span style={{fontSize: '13px', color: '#8c8c8c'}}>
                {record?.date} • {registrations.length} Students
              </span>
                </div>
              }
              style={{marginBottom: 24}}
          >
          {/* Key Metrics */}
          <Row gutter={[16, 16]}>
            <Col span={6}>
              <Card bordered={false} style={{ background: '#f0f5ff', height: '100%' }}>
                <div style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: '28px', fontWeight: 'bold', color: '#1890ff' }}>
                    {calculateStatistics(registrations).averageScore}%
                  </div>
                  <div style={{ fontSize: '14px', color: '#666' }}>Average Score</div>
                </div>
              </Card>
            </Col>
            <Col span={6}>
              <Card bordered={false} style={{ background: '#f6ffed', height: '100%' }}>
                <div style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: '28px', fontWeight: 'bold', color: '#52c41a' }}>
                    {calculateStatistics(registrations).passRate}%
                  </div>
                  <div style={{ fontSize: '14px', color: '#666' }}>Pass Rate</div>
                </div>
              </Card>
            </Col>
            <Col span={6}>
              <Card bordered={false} style={{ background: '#fff0f6', height: '100%' }}>
                <div style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: '28px', fontWeight: 'bold', color: '#eb2f96' }}>
                    {calculateStatistics(registrations).medianScore}%
                  </div>
                  <div style={{ fontSize: '14px', color: '#666' }}>Median Score</div>
                </div>
              </Card>
            </Col>
            <Col span={6}>
              <Card bordered={false} style={{ background: '#f0f2ff', height: '100%' }}>
                <div style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: '28px', fontWeight: 'bold', color: '#722ed1' }}>
                    ±{calculateStatistics(registrations).standardDeviation}
                  </div>
                  <div style={{ fontSize: '14px', color: '#666' }}>Std Deviation</div>
                </div>
              </Card>
            </Col>
          </Row>

          {/* Grade Distribution and Score Range */}
          <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
            <Col span={16}>
              <div style={{ background: '#fafafa', padding: '16px', borderRadius: '8px', height: '100%' }}>
                <div style={{ marginBottom: 16, fontSize: '14px', fontWeight: 'bold' }}>Grade Distribution</div>
                {Object.entries(calculateStatistics(registrations).gradeDistribution).map(([grade, count]) => {
                  const percentage = (count / registrations.length) * 100;
                  const colors = {
                    A: '#52c41a',
                    B: '#1890ff',
                    C: '#722ed1',
                    D: '#faad14',
                    F: '#f5222d'
                  };
                  return (
                      <div key={grade} style={{marginBottom: 8}}>
                        <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: 4}}>
                        <span style={{color: colors[grade], fontWeight: 'bold'}}>
                          Grade {grade} ({count})
                        </span>
                          <span style={{color: colors[grade]}}>{percentage.toFixed(1)}%</span>
                        </div>
                        <Progress
                            percent={percentage}
                            strokeColor={colors[grade]}
                            showInfo={false}
                            size="small"
                            strokeWidth={4}
                        />
                      </div>
                  );
                })}
              </div>
            </Col>
            <Col span={8}>
              <div style={{ background: '#fafafa', padding: '16px', borderRadius: '8px', height: '100%' }}>
                <div style={{ marginBottom: 16, fontSize: '14px', fontWeight: 'bold' }}>Score Range</div>
                <div style={{ textAlign: 'center', marginBottom: 16 }}>
                  <div style={{ fontSize: '24px', color: '#52c41a', fontWeight: 'bold' }}>
                    {calculateStatistics(registrations).highestScore}%
                  </div>
                  <div style={{ fontSize: '13px', color: '#666' }}>Highest Score</div>
                </div>
                <div style={{ textAlign: 'center', marginBottom: 16 }}>
                  <div style={{ fontSize: '24px', color: '#f5222d', fontWeight: 'bold' }}>
                    {calculateStatistics(registrations).lowestScore}%
                  </div>
                  <div style={{ fontSize: '13px', color: '#666' }}>Lowest Score</div>
                </div>
                <div style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: '24px', color: '#1890ff', fontWeight: 'bold' }}>
                    {calculateStatistics(registrations).highestScore - calculateStatistics(registrations).lowestScore}%
                  </div>
                  <div style={{ fontSize: '13px', color: '#666' }}>Score Range</div>
                </div>
              </div>
            </Col>
          </Row>
        </Card>
      )}

      {/* Student Registrations Section */}
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h3>Student Registrations</h3>
        <div style={{ display: 'flex', gap: '8px' }}>
          {editingKey ? (
            <>
              <Button
                type="primary"
                onClick={handleSaveEdit}
                loading={submitting}
              >
                Save Changes
              </Button>
              <Button onClick={() => {
                setEditingKey('');
                fetchRecordData();
              }}>
                Cancel
              </Button>
            </>
          ) : (
              <>
                <Button
                    type="default"
                    icon={<EditOutlined/>}
                    onClick={() => setEditingKey('all')}
                    disabled={showAddRow}
                >
                  Edit Registrations
                </Button>
                <Popover
                    content={csvTemplateContent}
                    title="CSV Template"
                    trigger="hover"
                    placement="bottom"
                >
                  <Upload
                      onChange={handleFileUpload}
                      accept=".csv"
                      showUploadList={false}
                  >
                    <Button icon={<UploadOutlined/>}>Import from CSV</Button>
                  </Upload>
                </Popover>
                <Button
                    type="primary"
                    icon={<PlusOutlined/>}
                    onClick={() => setShowAddRow(true)}
                    disabled={editingKey !== ''}
                >
                  Add Registration
                </Button>
              </>
          )}
        </div>
      </div>

      {showAddRow && (
          <div style={{
            marginBottom: 16,
            padding: 16,
            background: '#f5f5f5',
            borderRadius: '6px',
            border: '1px solid #d9d9d9'
          }}>
            <div style={{marginBottom: 8}}>
              <h4 style={{margin: '0 0 16px 0'}}>Add New Registration</h4>
              <div style={{display: 'flex', gap: '16px'}}>
                <Select
                    showSearch
                    style={{width: '70%'}}
                    placeholder="Select student"
                value={newRegistration.studentId}
                onChange={(value) => setNewRegistration({ ...newRegistration, studentId: value })}
                filterOption={(input, option) => {
                  const student = students.find(s => s.id === option.value);
                  return (
                    student?.firstName?.toLowerCase().includes(input.toLowerCase()) ||
                    student?.lastName?.toLowerCase().includes(input.toLowerCase()) ||
                    student?.id?.toString().includes(input) ||
                    student?.programOfStudy?.toLowerCase().includes(input.toLowerCase())
                  );
                }}
              >
                {students.map((student) => (
                  <Select.Option key={student.id} value={student.id}>
                    {`${student.id} - ${student.firstName} ${student.lastName} (${student.programOfStudy})`}
                  </Select.Option>
                ))}
              </Select>
              <InputNumber
                style={{ width: '30%' }}
                min={0}
                max={100}
                placeholder="Score"
                value={newRegistration.score}
                onChange={(value) => setNewRegistration({ ...newRegistration, score: value })}
              />
            </div>
            <div style={{ marginTop: 16, display: 'flex', justifyContent: 'flex-end', gap: '8px' }}>
              <Button onClick={() => {
                setShowAddRow(false);
                setNewRegistration({ studentId: null, score: null, isNew: true });
              }}>
                Cancel
              </Button>
              <Button
                type="primary"
                onClick={handleAddRegistration}
                loading={submitting}
              >
                Add Registration
              </Button>
            </div>
          </div>
        </div>
      )}

      <Table
          columns={[
            {
              title: 'Student ID',
              dataIndex: 'studentId',
              key: 'studentId',
              width: 120,
              fixed: true
            },
            {
              title: 'Student Name',
              key: 'studentName',
              width: 200,
              fixed: true,
              render: (_, record) => `${record.studentFirstName || ''} ${record.studentLastName || ''}`.trim() || '-'
            },
            {
              title: 'Score',
              dataIndex: 'score',
              key: 'score',
              width: 120,
              fixed: true,
              render: (text, record) => {
                return editingKey === 'all' ? (
                    <InputNumber
                        min={0}
                        max={100}
                        style={{width: '100%'}}
                  defaultValue={record.score}
                  onChange={(value) => {
                    record.score = value;
                  }}
                  onClick={(e) => e.stopPropagation()}
                />
              ) : (
                text
              );
            }
          },
          {
            title: editingKey === 'all' ? 'Action' : 'Grade',
            key: 'action',
            width: 100,
            fixed: true,
            render: (_, record) => {
              if (editingKey === 'all') {
                return (
                  <Button
                    danger
                    type="link"
                    icon={<DeleteOutlined />}
                    onClick={(e) => {
                      e.stopPropagation();
                      Modal.confirm({
                        title: 'Delete Registration',
                        content: 'Are you sure you want to delete this registration?',
                        okText: 'Yes',
                        okType: 'danger',
                        cancelText: 'No',
                        onOk: () => handleDeleteRegistration(record.id)
                      });
                    }}
                  />
                );
              }
              return calculateUKGrade(record.score);
            }
          },
            {
              title: 'Registration Time',
              dataIndex: 'registrationTime',
              key: 'registrationTime',
              width: 180,
              fixed: true,
              render: (time) => time ? new Date(time).toLocaleString() : '-'
            }
          ]}
        dataSource={registrations}
        rowKey="id"
        pagination={{ pageSize: 10 }}
        scroll={{ x: 720 }} // Add horizontal scroll if needed
        onRow={(record) => ({
          onClick: () => {
            if (editingKey !== 'all') {
              const student = {
                id: record.studentId,
                firstName: record.studentFirstName,
                lastName: record.studentLastName
              };
              onStudentClick(student);
              setSelectedKey('student-record');
            }
          },
          style: {
            cursor: editingKey === 'all' ? 'default' : 'pointer'
          }
        })}
      />

      {/* Add Delete Confirmation Modal */}
      <Modal
        title="Delete Record"
        visible={isDeleteModalVisible}
        onOk={handleDelete}
        onCancel={() => setIsDeleteModalVisible(false)}
        okText="Delete"
        okButtonProps={{ danger: true }}
        cancelText="Cancel"
      >
        <p>Are you sure you want to delete this record?</p>
        <p>This will permanently delete:</p>
        <ul>
          <li>The exam record for {record?.moduleCode} - {record?.moduleName}</li>
          <li>All student grades associated with this record</li>
          <li>All statistical data for this exam</li>
        </ul>
        <p style={{ color: '#ff4d4f', marginTop: 16 }}>
          This action cannot be undone.
        </p>
      </Modal>

      {/* Add Edit Record Modal */}
      <Modal
        title="Edit Record"
        open={isEditModalOpen}
        onOk={handleEditRecord}
        onCancel={() => setIsEditModalOpen(false)}
        confirmLoading={submitting}
        width={600}
      >
        <Form form={editForm} layout="vertical">
          <Form.Item
            name="moduleCode"
            label="Module"
            rules={[{ required: true, message: 'Please select a module' }]}
          >
            <Select
              showSearch
              placeholder="Search for module"
              disabled // Disable module change
              filterOption={(input, option) =>
                option.children.toLowerCase().includes(input.toLowerCase())
              }
            >
              <Option value={record?.moduleCode}>
                {`${record?.moduleCode} - ${record?.moduleName}`}
              </Option>
            </Select>
          </Form.Item>
          <Form.Item
              name="date"
              label="Date"
              rules={[{required: true, message: 'Please select a date'}]}
          >
            <DatePicker style={{width: '100%'}}/>
          </Form.Item>
        </Form>
      </Modal>
    </Content>
  );
};

export default RecordDetail;
