import React, {useEffect, useState} from 'react';
import {
  Badge,
  Button,
  DatePicker,
  Form,
  InputNumber,
  Layout,
  message,
  Modal,
  Popover,
  Select,
  Table,
  Upload
} from 'antd';
import {
  DeleteOutlined,
  DownloadOutlined,
  FilterOutlined,
  PlusOutlined,
  ReloadOutlined,
  UploadOutlined
} from '@ant-design/icons';
import {v4 as uuidv4} from 'uuid';
import api from '../api';

const {Content} = Layout;

const GradeRecords = ({setSelectedKey, onStudentClick}) => {
  const [records, setRecords] = useState([]);
  const [modules, setModules] = useState([]);
  const [students, setStudents] = useState([]);
  const [pagination, setPagination] = useState({current: 1, pageSize: 10, total: 0});
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [form] = Form.useForm();
  const [marks, setMarks] = useState([{ key: uuidv4(), studentId: null, score: null }]); // State to manage student marks
  const [filterValues, setFilterValues] = useState({
    moduleCode: '',
    year: '',
    month: ''
  });

  // Move handleResetFilters before its usage
  const handleResetFilters = () => {
    const emptyFilters = {
      moduleCode: '',
      year: '',
      month: ''
    };
    setFilterValues(emptyFilters);
    fetchRecords(emptyFilters);
  };

  // Fetch grade records
  const fetchRecords = async (params = {}) => {
    setLoading(true);
    try {
      const response = await api.get('/api/record', {
        params: {
          current: params.current || pagination.current,
          size: params.pageSize || pagination.pageSize,
          moduleCode: params.moduleCode || filterValues.moduleCode,
          year: params.year || filterValues.year,
          month: params.month || filterValues.month
        },
      });
      if (response.data?.data?.records) {
        setRecords(response.data.data.records);
        setPagination({
          ...pagination,
          total: response.data.data.total,
          current: response.data.data.current,
          pageSize: response.data.data.size,
        });
      }
    } catch (error) {
      console.error('Error fetching records:', error);
      message.error('Failed to load records');
    } finally {
      setLoading(false);
    }
  };

  // Fetch modules
  const fetchModules = async () => {
    setLoading(true);
    try {
      const response = await api.get('/api/module/list', {
        params: {
          current: 1,
          size: 100,  // Fetch more modules at once
          code: '',   // Empty string to fetch all
          name: '',   // Empty string to fetch all
          leader: ''  // Empty string to fetch all
        }
      });
      if (response.data?.data?.records) {
        const records = response.data.data.records.map((module) => ({
          key: module.id,
          code: module.code,
          name: module.name,
        }));
        setModules(records);
      }
    } catch (error) {
      console.error('Error fetching modules:', error);
      message.error('Failed to load modules');
    } finally {
      setLoading(false);
    }
  };

  // Fetch students
  const fetchStudents = async () => {
    setLoading(true);
    try {
      const response = await api.get('/api/student/list-students', {
        params: {
          current: 1,
          size: 100,  // Fetch more students at once
          id: '',     // Empty string to fetch all
          firstName: '', // Empty string to fetch all
          lastName: '',  // Empty string to fetch all
          username: '',  // Empty string to fetch all
          programOfStudy: '' // Empty string to fetch all
        }
      });
      setStudents(response.data?.data?.records || []);
    } catch (error) {
      console.error('Error fetching students:', error);
      message.error('Failed to load students');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRecords();
    fetchModules();
    fetchStudents();
  }, []);

  const showAddRecordModal = () => {
    setIsModalOpen(true);
  };

  const handleCancel = () => {
    setIsModalOpen(false);
    setMarks([{ key: uuidv4(), studentId: null, score: null }]); // Reset marks when modal closes
    form.resetFields();
  };

  const handleAddRecord = async () => {
    try {
      const values = await form.validateFields();
      if (marks.some((mark) => !mark.studentId || mark.score === null)) {
        message.error('Please complete all student marks before submitting');
        return;
      }

      setSubmitting(true);

      // First create the record
      const recordResponse = await api.post('/api/record', {
        moduleCode: values.module,
        date: values.date.format('YYYY-MM-DD')
      });

      if (recordResponse.data?.code === 200) {
        // Get the record ID from the response data directly
        const recordId = recordResponse.data.data.id;

        // Create registrations for each student
        for (const mark of marks) {
          const registrationResponse = await api.post('/api/registration/add', {
            studentId: parseInt(mark.studentId),
            recordId: parseInt(recordId),
            score: parseInt(mark.score)
          });

          if (registrationResponse.data?.code !== 200) {
            throw new Error(`Failed to add registration for student ${mark.studentId}`);
          }
        }

        message.success('Record and registrations added successfully');
        setIsModalOpen(false);
        setMarks([{ key: uuidv4(), studentId: null, score: null }]);
        form.resetFields();
        fetchRecords(); // Refresh the records list
      } else {
        throw new Error(recordResponse.data?.message || 'Failed to create record');
      }
    } catch (error) {
      console.error('Error in handleAddRecord:', error);
      message.error('Failed to add record: ' + (error.message || 'Unknown error'));
    } finally {
      setSubmitting(false);
    }
  };

  const handleAddRow = () => {
    setMarks([...marks, { key: uuidv4(), studentId: null, score: null }]);
  };

  const handleRemoveRow = (key) => {
    setMarks(marks.filter((mark) => mark.key !== key));
  };

  const columns = [
    {
      title: 'Record ID',
      dataIndex: 'id',
      key: 'id',
      width: 150
    },
    {
      title: 'Module Code',
      dataIndex: 'moduleCode',
      key: 'moduleCode'
    },
    {
      title: 'Module Name',
      dataIndex: 'moduleName',
      key: 'moduleName'
    },
    {
      title: 'Date',
      dataIndex: 'date',
      key: 'date'
    },
    {
      title: 'Average Score',
      dataIndex: 'averageScore',
      key: 'averageScore',
      render: (score) => score ? `${score.toFixed(2)}%` : '-'
    },
  ];

  const csvTemplateContent = (
      <div style={{maxWidth: '300px'}}>
        <p>Please download and follow the CSV template format for importing grade records:</p>
        <a
            href="./files/RecordDataTemplate.csv"
            download="Record Data Template.csv"
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
        Note: The CSV file must match the template format exactly.
        <ul style={{ marginTop: '4px', paddingLeft: '16px' }}>
          <li>moduleCode: The code of the module</li>
          <li>date: Format YYYY-MM-DD</li>
          <li>studentId: Valid student ID</li>
          <li>score: Number between 0-100</li>
        </ul>
      </p>
    </div>
  );

  const handleFileUpload = async ({ file, onSuccess, onError }) => {
    try {
      const formData = new FormData();
      formData.append('file', file);

      const response = await api.post('/api/record/import-records', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      if (response.data?.code === 200) {
        message.success('Records imported successfully');
        onSuccess('ok');
        fetchRecords();
      } else {
        throw new Error(response.data?.message || 'Import failed');
      }
    } catch (error) {
      console.error('Error importing records:', error);
      message.error(error.response?.data?.message || 'Failed to import records');
      onError(error);
    }
  };

  // Add filter content
  const filterContent = (
    <div style={{ padding: '8px', minWidth: '200px' }}>
      <div style={{ marginBottom: '16px' }}>
        <div style={{ marginBottom: '8px' }}>Module</div>
        <Select
          style={{ width: '100%' }}
          placeholder="Select Module"
          value={filterValues.moduleCode}
          onChange={(value) => handleFilterChange('moduleCode', value)}
          allowClear
        >
          {modules.map((module) => (
            <Select.Option key={module.code} value={module.code}>
              {`${module.code} - ${module.name}`}
            </Select.Option>
          ))}
        </Select>
      </div>

      <div style={{ marginBottom: '16px' }}>
        <div style={{ marginBottom: '8px' }}>Year</div>
        <Select
          style={{ width: '100%' }}
          placeholder="Select Year"
          value={filterValues.year}
          onChange={(value) => handleFilterChange('year', value)}
          allowClear
        >
          {Array.from({ length: 5 }, (_, i) => {
            const year = new Date().getFullYear() - i;
            return (
              <Select.Option key={year} value={year}>
                {year}
              </Select.Option>
            );
          })}
        </Select>
      </div>

      <div style={{ marginBottom: '16px' }}>
        <div style={{ marginBottom: '8px' }}>Month</div>
        <Select
          style={{ width: '100%' }}
          placeholder="Select Month"
          value={filterValues.month}
          onChange={(value) => handleFilterChange('month', value)}
          allowClear
        >
          {Array.from({ length: 12 }, (_, i) => (
            <Select.Option key={i + 1} value={i + 1}>
              {new Date(2000, i, 1).toLocaleString('default', { month: 'long' })}
            </Select.Option>
          ))}
        </Select>
      </div>

      <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '8px' }}>
        <Button onClick={handleResetFilters}>Reset</Button>
        <Button type="primary" onClick={() => fetchRecords(filterValues)}>
          Apply
        </Button>
      </div>
    </div>
  );

  // Add filter handlers
  const handleFilterChange = (key, value) => {
    const newFilterValues = {
      ...filterValues,
      [key]: value
    };
    setFilterValues(newFilterValues);
    fetchRecords(newFilterValues);
  };

  return (
    <Content style={{ margin: '24px 16px 0', padding: 16, background: '#fff' }}>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>Grade Records</h2>
        <div style={{ display: 'flex', gap: '8px' }}>
          <Button icon={<ReloadOutlined/>} onClick={fetchRecords}/>
          <Popover
              content={filterContent}
              title="Filter Records"
              trigger="click"
              placement="bottomRight"
          >
            <Button icon={<FilterOutlined/>}>
              Filters
              {Object.values(filterValues).some(x => x) && (
                  <Badge status="processing" style={{marginLeft: '4px'}}/>
              )}
            </Button>
          </Popover>
          <Popover
              content={csvTemplateContent}
              title="CSV Template"
              trigger="hover"
              placement="bottom"
          >
            <Upload
                accept=".csv"
                showUploadList={false}
                customRequest={handleFileUpload}
                beforeUpload={(file) => {
                const isCsv = file.type === 'text/csv' || file.name.endsWith('.csv');
                if (!isCsv) {
                  message.error('You can only upload CSV files!');
                  return Upload.LIST_IGNORE;
                }
                return true;
              }}
            >
              <Button icon={<UploadOutlined />}>Import from CSV</Button>
            </Upload>
          </Popover>
          <Button type="primary" icon={<PlusOutlined />} onClick={showAddRecordModal}>
            Add Record
          </Button>
        </div>
      </div>
      <Table
        columns={columns}
        dataSource={records}
        loading={loading}
        rowKey="id"
        pagination={{
          current: pagination.current,
          pageSize: pagination.pageSize,
          total: pagination.total,
          showSizeChanger: true,
        }}
        onChange={(newPagination) => {
          fetchRecords({
            current: newPagination.current,
            pageSize: newPagination.pageSize,
            moduleCode: filterValues.moduleCode,
            year: filterValues.year,
            month: filterValues.month
          });
        }}
        onRow={(record) => ({
          onClick: () => setSelectedKey(['recordDetail', record.id]),
          style: { cursor: 'pointer' }
        })}
      />

      {/* Add Record Modal */}
      <Modal
        title="Add Record"
        visible={isModalOpen}
        onOk={handleAddRecord}
        onCancel={handleCancel}
        confirmLoading={submitting}
        width={800}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="module"
            label="Module"
            rules={[{ required: true, message: 'Please select a module' }]}
          >
            <Select
              showSearch
              placeholder="Search for module"
              filterOption={(input, option) => {
                const moduleText = option.children?.toLowerCase() || '';
                return moduleText.includes(input.toLowerCase());
              }}
              optionFilterProp="children"
            >
              {modules.map((module) => (
                <Select.Option key={module.key} value={module.code}>
                  {`${module.code} - ${module.name}`}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item
            name="date"
            label="Date"
            rules={[{ required: true, message: 'Please select a date' }]}
          >
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
        </Form>
        <Table
            columns={[
            {
              title: 'Student',
              dataIndex: 'studentId',
              key: 'studentId',
              width: '60%',
              render: (_, record) => (
                <Select
                  showSearch
                  placeholder="Search by ID, name, or programme"
                  style={{ width: '100%' }}
                  value={record.studentId}
                  onChange={(value) => {
                    const updatedMarks = marks.map((mark) =>
                      mark.key === record.key ? { ...mark, studentId: value } : mark
                    );
                    setMarks(updatedMarks);
                  }}
                  filterOption={(input, option) => {
                    const student = students.find(s => s.id === option.value);
                    return (
                      student?.firstName?.toLowerCase().includes(input.toLowerCase()) ||
                      student?.lastName?.toLowerCase().includes(input.toLowerCase()) ||
                      student?.id?.toString().includes(input) ||
                      student?.programOfStudy?.toLowerCase().includes(input.toLowerCase())
                    );
                  }}
                  optionFilterProp="children"
                  dropdownStyle={{ width: '400px' }}
                >
                  {students.map((student) => (
                      <Select.Option
                          key={student.id}
                          value={student.id}
                      >
                        <div style={{
                          display: 'flex',
                          justifyContent: 'space-between',
                          padding: '2px 0',
                          fontSize: '13px'
                        }}>
                          <div>
                          <span style={{fontWeight: 'bold'}}>
                            {student.id}
                          </span>
                            {' - '}
                            <span>
                            {student.firstName} {student.lastName}
                          </span>
                          </div>
                          <div style={{
                            color: '#666',
                            fontSize: '11px',
                            backgroundColor: '#f5f5f5',
                            padding: '0 4px',
                            borderRadius: '2px',
                            marginLeft: '8px'
                          }}>
                            {student.programOfStudy}
                          </div>
                      </div>
                    </Select.Option>
                  ))}
                </Select>
              ),
            },
            {
              title: 'Score',
              dataIndex: 'score',
              key: 'score',
              width: '30%',
              render: (_, record) => (
                <InputNumber
                  min={0}
                  max={100}
                  value={record.score}
                  style={{ width: '100%' }}
                  onChange={(value) => {
                    const updatedMarks = marks.map((mark) =>
                      mark.key === record.key ? { ...mark, score: value } : mark
                    );
                    setMarks(updatedMarks);
                  }}
                  placeholder="0-100"
                />
              ),
            },
            {
              title: 'Action',
              key: 'action',
              width: '10%',
              render: (_, record) => (
                <Button
                  danger
                  type="text"
                  icon={<DeleteOutlined />}
                  onClick={() => handleRemoveRow(record.key)}
                />
              ),
            }
          ]}
            dataSource={marks}
            pagination={false}
            rowKey="key"
            footer={() => (
                <Button
                    type="dashed"
                    icon={<PlusOutlined/>}
                    onClick={handleAddRow}
                    block
                    style={{marginTop: '8px'}}
                >
                  Add Student
                </Button>
            )}
        />
      </Modal>
    </Content>
  );
};

export default GradeRecords;
